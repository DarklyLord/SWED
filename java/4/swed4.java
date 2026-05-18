import java.util.Scanner;
import java.util.Random;
import java.time.LocalDateTime;

public class swed4 {
	public static void main(String[] args) {
		Interface userInterface = new Interface();
		Userdata userdata1 = userInterface.createFromConsole();
		userInterface.login(userdata1);

		WebsiteTimer websiteTimer = new WebsiteTimer(userdata1.getWebsitehook());
		websiteTimer.start();
		userInterface.manageSubscriptions(userdata1, websiteTimer);

		System.out.println(userdata1.getUsername());
		System.out.println(userdata1.getPassword());
		System.out.println(userdata1.getUserID());
		System.out.println(userdata1.getNotificationChnannel());
	}
}

class Interface {
	public Userdata createFromConsole() {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Username: ");
		String username = scanner.nextLine();

		System.out.print("Password: ");
		String password = scanner.nextLine();

		Integer userID = new Random().nextInt(2) + 1;
        
		System.out.print("Notification channel (email/phonenumber): ");
		String notificationChnannel = scanner.nextLine();

        System.out.print("Website URL: ");
        String websiteUrl = scanner.nextLine();

		System.out.print("Frequency in minutes: ");
		Integer frequencyMinutes = Integer.valueOf(scanner.nextLine());

		return new Userdata(username, password, userID, notificationChnannel, websiteUrl, frequencyMinutes);
	}

	public void login(Userdata existingUser) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Username: ");
		String username = scanner.nextLine();

		System.out.print("Password: ");
		String password = scanner.nextLine();

		Integer userID = existingUser.getUserID();

		if (existingUser.getUsername().equals(username)
				&& existingUser.getPassword().equals(password)
				&& existingUser.getUserID().equals(userID)
				) {
			    System.out.println("logged in");
                    System.out.print("Notification channel (email/phonenumber) (Blank for no change): ");
                    String notificationChnannel = scanner.nextLine();
                    if (!notificationChnannel.isBlank()) {
                        existingUser.setNotificationChnannel(notificationChnannel);
                    }
                    System.out.print("Frequency in minutes (Blank for no change): ");
                    String interval = scanner.nextLine();
                    if (!interval.isBlank()) {
                        existingUser.getWebsitehook().setFrequencyMinutes(Integer.valueOf(interval));
                    }
		} else {
			System.out.println("login failed");
		}
	}

	public void manageSubscriptions(Userdata user, WebsiteTimer websiteTimer) {
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.print("Manage subscription (modify/cancel/exit): ");
			String action = scanner.nextLine().trim().toLowerCase();

			if (action.equals("modify")) {
				modifySubscription(user, websiteTimer);
			} else if (action.equals("cancel")) {
				cancelSubscription(user, websiteTimer);
			} else if (action.equals("exit")) {
				return;
			} else {
				System.out.println("Unknown option");
			}
		}
	}

	public void modifySubscription(Userdata user, WebsiteTimer websiteTimer) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("New website URL (blank to keep current): ");
		String websiteUrl = scanner.nextLine();
		if (!websiteUrl.isBlank()) {
			user.getWebsitehook().setWebsiteUrl(websiteUrl);
		}

		System.out.print("New frequency in minutes (blank to keep current): ");
		String frequencyInput = scanner.nextLine();
		if (!frequencyInput.isBlank()) {
			user.getWebsitehook().setFrequencyMinutes(Integer.valueOf(frequencyInput));
		}

		System.out.print("New notification channel (blank to keep current): ");
		String notificationChannel = scanner.nextLine();
		if (!notificationChannel.isBlank()) {
			user.setNotificationChnannel(notificationChannel);
			user.getWebsitehook().setNotificationChannel(notificationChannel);
		}

		websiteTimer.restart();
		System.out.println("subscription updated");
	}

	public void cancelSubscription(Userdata user, WebsiteTimer websiteTimer) {
		user.getWebsitehook().setActive(false);
		websiteTimer.stop();
		System.out.println("subscription cancelled");
	}

}

class Userdata {
	private String username;
	private String password;
	private Integer userID;
	private String notificationChnannel;
	private Websitehook websitehook;
	private Notifier notifier;

	public Userdata(String username, String password, Integer userID, String notificationChnannel, String websiteUrl, Integer frequencyMinutes) {
		setUsername(username);
		setPassword(password);
		setUserID(userID);
		setNotificationChnannel(notificationChnannel);
		this.notifier = new Notifier();
		this.websitehook = new Websitehook(websiteUrl, frequencyMinutes, notificationChnannel, this);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		validateRequiredString(username, "Username");
		this.username = username;
	}

	public void setPassword(String password) {
		validateRequiredString(password, "Password");
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		if (userID == null) {
			throw new IllegalArgumentException("userID cannot be null.");
		}
		this.userID = userID;
	}

	public String getNotificationChnannel() {
		return notificationChnannel;
	}

	public void setNotificationChnannel(String notificationChnannel) {
		validateRequiredString(notificationChnannel, "notificationChnannel");
		this.notificationChnannel = notificationChnannel;
	}

	public Websitehook getWebsitehook() {
		return websitehook;
	}

	public void onWebsiteUpdated(String websiteUrl, String status, boolean changed) {
		notifier.notify("console", "Checked " + websiteUrl + ": " + status);
		if (changed) {
			notifier.notify(notificationChnannel, "Notification via " + notificationChnannel + ": update detected for " + websiteUrl);
		}
	}

	private void validateRequiredString(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " cannot be null or blank.");
		}
	}
}


class WebsiteTimer {
	private final Websitehook websitehook;
	private java.util.Timer timer;

	public WebsiteTimer(Websitehook websitehook) {
		this.websitehook = websitehook;
		this.timer = new java.util.Timer(true);
	}

	public void start() {
		if (!websitehook.isActive()) {
			return;
		}

		this.timer = new java.util.Timer(true);

		timer.scheduleAtFixedRate(new java.util.TimerTask() {
			@Override
			public void run() {
				websitehook.checkForChanges();
			}
		}, 0L, websitehook.getFrequencyMinutes() * 60_000L);
	}

	public void restart() {
		stop();
		websitehook.setActive(true);
		start();
	}

	public void stop() {
		timer.cancel();
	}
}


class Websitehook {
    private String websiteUrl;
    private Integer frequencyMinutes;
    private String notificationChannel;
    private String lastchanged;
	private boolean active;
	private Userdata userdata;

	public Websitehook(String websiteUrl, Integer frequencyMinutes, String notificationChannel, Userdata userdata) {
        setWebsiteUrl(websiteUrl);
		setFrequencyMinutes(frequencyMinutes);
		setNotificationChannel(notificationChannel);
		this.lastchanged = "0";
		this.active = true;
		this.userdata = userdata;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        validateRequiredString(websiteUrl, "websiteUrl");
        this.websiteUrl = websiteUrl;
    }

	public Integer getFrequencyMinutes() {
		return frequencyMinutes;
    }

	public void setFrequencyMinutes(Integer frequencyMinutes) {
		if (frequencyMinutes == null || frequencyMinutes <= 0) {
			throw new IllegalArgumentException("frequencyMinutes must be greater than 0.");
		}
		this.frequencyMinutes = frequencyMinutes;
	}

	public String getNotificationChannel() {
		return notificationChannel;
	}

	public void setNotificationChannel(String notificationChannel) {
		validateRequiredString(notificationChannel, "notificationChannel");
		this.notificationChannel = notificationChannel;
    }

    public String getLastchanged() {
        return lastchanged;
    }

    public void setLastchanged(String lastchanged) {
        validateRequiredString(lastchanged, "lastchanged");
        this.lastchanged = lastchanged;
    }

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void checkForChanges() {
		if (!active) {
			return;
		}

		boolean changed = new Random().nextBoolean();
		String status = changed ? "changed" : "not changed";
		String timestamp = LocalDateTime.now().toString();
		setLastchanged(timestamp);
		userdata.onWebsiteUpdated(websiteUrl, status, changed);
	}

    private void validateRequiredString(String value, String fieldName) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or blank.");
        }
    }

}

class Notifier {
    private String channel;
    private String message = "Default notification message";
    
	public void notify(String channel, String message) {
		this.channel = channel;
		this.message = message;
		System.out.println(message);
	}
}
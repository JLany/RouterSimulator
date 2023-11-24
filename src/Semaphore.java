import javax.print.attribute.standard.PrinterURI;
import java.io.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

class Semaphore {
    private int size;

    public Semaphore(int size) {
        this.size = size;
    }

    public synchronized void acquire(Device device) {
        WriteToFile writer = new WriteToFile();

        size--;
        if (size < 0) {
            writer.write("logs.txt", "- (" + device.getDeviceName() + ") (" + device.getType() + ") arrived and waiting\n");
            try {
                wait();
            } catch (InterruptedException ignored) {

            }
        } else {
            writer.write("logs.txt", "- (" + device.getDeviceName() + ") (" + device.getType() + ") arrived\n");
        }
    }

    public synchronized void release() {
        size++;
        notify();
    }
}

class WriteToFile {
    public void write(String fileName, String str)
    {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            out.write(str);
            out.close();
        } catch (IOException e) {
            System.out.println("exception occurred" + e);
        }
    }
}
enum DeviceType {
    Mobile, PC, Tablet
}

class Device extends Thread {
    WriteToFile writeToFile = new WriteToFile();
    private String deviceName;
    private DeviceType type;
    private Router router;
    private int index;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Device(String deviceName, DeviceType type, Router router) {
        this.router = router;
        this.deviceName = deviceName;
        this.type = type;
    }

    @Override
    public void run() {
        router.connect(this);

        login();

        try {
            Thread.sleep((new Random()).nextInt(100, 1000));
        } catch (Exception e) {

        }

        performOnlineActivity();

        try {
            Thread.sleep((new Random()).nextInt(100, 1000));
        } catch (Exception e) {

        }

        logout();

        router.release(index);
    }

    public void login() {
        String text = "- Connection " + (index + 1) + ": " + deviceName + " Login\n";
        writeToFile.write("logs.txt", text);
    }

    public void performOnlineActivity() {
        String text = "- Connection " + (index + 1) + ": " + deviceName + " Performs Online Activity\n";
        writeToFile.write("logs.txt", text);
    }

    public void logout() {
        String text = "- Connection " + (index + 1) + ": " + deviceName + " Logged Out\n";
        writeToFile.write("logs.txt", text);
    }
}

class Router {
    WriteToFile writeToFile = new WriteToFile();
    Device[] devices;
    Semaphore semaphore;
    int connectionsNum;

    public Router(int connectionsNum) {
        this.devices = new Device[connectionsNum];
        this.semaphore = new Semaphore(connectionsNum);
        this.connectionsNum = connectionsNum;
        for (int i = 0; i < connectionsNum; i++) {
            devices[i] = null;
        }
    }

    public void resetConnection(int i) {
        devices[i] = null;
    }

    public void connect(Device curD) {
        semaphore.acquire(curD);

        for (int i = 0; i < connectionsNum; i++) {
            if (devices[i] == null) {
                devices[i] = curD;
                curD.setIndex(i);
                String text = "- Connection " + (i + 1) + ": " + curD.getDeviceName() + " Occupied\n";
                writeToFile.write("logs.txt", text);
                break;
            }
        }
    }

    public void release(int index) {
        resetConnection(index);

        semaphore.release();
    }
}

class Network {
    private static File file;
    private static void createFile() throws IOException {
        file = new File("logs.txt");
        file.createNewFile();
        // empty files content
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        createFile();
        Scanner myScanner = new Scanner(System.in);
        System.out.println("What is the number of WI-FI Connections?");
        int connectionsNum = myScanner.nextInt();
        myScanner.nextLine();
        System.out.println("What is the number of devices Clients want to connect?");
        int devicesNum = myScanner.nextInt();
        myScanner.nextLine();
        Router router = new Router(connectionsNum);
        ArrayList<Device> devices = new ArrayList<>();
        for (int i = 0; i < devicesNum; i++) {
            String deviceInfo = myScanner.nextLine();
            String[] info = deviceInfo.split(" ");
            DeviceType deviceType = null;
            info[1] = info[1].toLowerCase();
            switch (info[1]) {
                case "mobile": {
                    deviceType = DeviceType.Mobile;
                    break;
                }
                case "pc": {
                    deviceType = DeviceType.PC;
                    break;
                }
                case "tablet": {
                    deviceType = DeviceType.Tablet;
                    break;
                }
                default: {
                    System.out.println("Invalid Type!");
                    return;
                }
            }
            devices.add(new Device(info[0], deviceType, router));
        }
        for (Device device : devices) {
            device.start();
        }
    }
}

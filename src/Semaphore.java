import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

//public class Semaphore {
//    private int value;
//
//
//    public void do() {
//        Object.wait();
//    }
//    // => th1 | th2 | th3 | th4
//    public void release() {
//        notify();
//    }
//}

enum DeviceType {
    Mobile, PC, Tablet
}

class Device extends Thread {
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
        router.addDevice(this);
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
        router.resetConnection(index);
        logout();
        router.release();
    }

    public void login() {
        System.out.println("- Connection " + (index + 1) + ": " + deviceName + " Login");
    }

    public void performOnlineActivity() {
        System.out.println("- Connection " + (index + 1) + ": " + deviceName + " Performs Online Activity");
    }

    public void logout() {
        System.out.println("- Connection " + (index + 1) + ": " + deviceName + " Logged out");

    }
}

class Router {
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

    public void addDevice(Device curD) {
        connect();
        //System.out.println("(" + curD.getName() + ") (" + curD.getType() + "arrived");
        for (int i = 0; i < connectionsNum; i++) {
            if (devices[i] == null) {
                devices[i] = curD;
                curD.setIndex(i);
                System.out.println("- Connection " + (i + 1) + ": " + curD.getDeviceName() + " Occupied");
                break;
            }
        }
    }

    public void resetConnection(int i) {
        devices[i] = null;
    }

    public void connect() {
        try {
            // System.out.println("(" + curD.getName() + ") (" + curD.getType() + "arrived and waiting");
            semaphore.acquire();
        } catch (Exception e) {

        }
    }

    public void release() {
        try {
            semaphore.release();
        } catch (Exception e) {

        }
    }
}

class Network {
    public static void main(String[] args) {
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
            switch (info[1]) {
                case "Mobile": {
                    deviceType = DeviceType.Mobile;
                    break;
                }
                case "PC": {
                    deviceType = DeviceType.PC;
                    break;
                }
                case "Tablet": {
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

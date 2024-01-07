import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class MagicPacketSender {
	
	public static void sendMagicPacket(String ipAddress, String macAddress) {
        try {
            byte[] macBytes = getMacBytes(macAddress);
            byte[] magicPacket = createMagicPacket(macBytes);

            InetAddress address = InetAddress.getByName(ipAddress);
            DatagramPacket packet = new DatagramPacket(magicPacket, magicPacket.length, address, 9);

            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();

            System.out.println("Magic packet sent successfully.");
        } catch (Exception e) {
            System.out.println("Failed to send magic packet: " + e.getMessage());
        }
    }

    private static byte[] getMacBytes(String macAddress) throws IllegalArgumentException {
        String[] hexDigits = macAddress.split("(\\:|\\-)");
        if (hexDigits.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address format");
        }

        byte[] macBytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            macBytes[i] = (byte) Integer.parseInt(hexDigits[i], 16);
        }

        return macBytes;
    }

    private static byte[] createMagicPacket(byte[] macBytes) {
        byte[] magicPacket = new byte[102];

        for (int i = 0; i < 6; i++) {
            magicPacket[i] = (byte) 0xFF;
        }

        for (int i = 6; i < magicPacket.length; i += 6) {
            System.arraycopy(macBytes, 0, magicPacket, i, 6);
        }

        return magicPacket;
    }

    public static void main(String[] args) {
        String ipAddress = "10.11.150.22"; // 远程IP地址（广播地址）
        String macAddress = "00:e0:4c:68:3f:f3"; // 目标设备的MAC地址

        sendMagicPacket(ipAddress, macAddress);
    }
    
}

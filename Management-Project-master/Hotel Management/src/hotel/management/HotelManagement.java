package hotel.management;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HotelManagement {

    private static DAO dbs = new Query();
    Authentication a = new Authentication();
    Exception exp = new Exception();
    Scanner sc = new Scanner(System.in);
    static Socket socket;
    static DataOutputStream outStream;

    public static void main(String[] args) {
        try {
            socket = new Socket("127.0.0.2", 3030);
            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String clientMessage = "", serverMessage = "";
            while (!clientMessage.equals("bye")) {
                HotelManagement hotel = new HotelManagement();
                hotel.login();
            }
            outStream.close();
            outStream.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void login() {

        System.out.println("Hotel Management System");
        String Id;
        int userId = 0;
        do {
            System.out.print("Enter a Valid User Id: ");
            Id = sc.nextLine();
            try {
                userId = Integer.parseInt(Id);
            } catch (NumberFormatException e) {
                Id = "invalid";
            }
        } while (Id.equals("invalid"));
        System.out.print("Enter Password:");
        String password = sc.nextLine();
        if (a.auhtenticate(userId, password)) {
            mainMenu();
        } else {
            System.out.println("Invalid User Name or Password!");
        }

    }

    public void bookRoom() {
        int no_of_rooms;
        System.out.println("Hotel Management System");
        System.out.print("Enter No of Guests:");
        int no_of_Guests = sc.nextInt();

        no_of_rooms = (no_of_Guests / 2) + no_of_Guests % 2;
        List<Room> room = new Query().getRooms(no_of_rooms, 0);
        for (int i = 0; i < room.size(); i++) {
            System.out.println((1 + i) + ")" + room.get(i).getRoomType());
        }
        System.out.print("Select ROOM TYPE:");
        int room_type = sc.nextInt();
        System.out.print("no of days to be booked for:");
        int no_of_days = sc.nextInt();
        double rate = no_of_rooms * room.get(room_type - 1).getRate() * no_of_days;
        System.out.println(rate);
        System.out.print("Confirm Booking (Y/N)");
        sc.nextLine();
        String confirm = sc.nextLine();
        if (confirm.equals("Y")) {
            System.out.print("Title:");
            String title = sc.nextLine();
            System.out.print("First Name:");
            String first_name = sc.nextLine();
            System.out.print("Last Name:");
            String last_name = sc.nextLine();
            System.out.print("Address:");
            String address = sc.nextLine();
            System.out.print("Phone No:");
            int phone = sc.nextInt();
            System.out.print("email:");
            sc.nextLine();
            String email = sc.nextLine();
            int guest_id = new Query().insertGuest(title, first_name, last_name, address, phone, email);
            new Query().placeBooking(guest_id, room.get(room_type - 1).getRoomType(), no_of_rooms, no_of_days, rate);
            mainMenu();
        }
    }

    public void billService() {
        System.out.println("Hotel Management System");
        System.out.print("Enter Booking Id:");
        int book_id = sc.nextInt();
        Reservation reservation = new Query().getReservation(book_id);
        System.out.print("test:" + reservation.getGuest_id());
        String Guest = new Query().getGuest(reservation.getGuest_id());
        System.out.println("Guest name:" + Guest);
        System.out.println("no of rooms booked:" + reservation.getNo_of_rooms());
        System.out.println("rate per night" + reservation.getBill() / reservation.getNo_of_days());
        System.out.println("Type of room:" + reservation.getRoom_type());
        System.out.println("Total amount:" + reservation.getBill());
        if (reservation.getStatus().equalsIgnoreCase("checked out")) {
            System.out.print("person has checked out  enter any key to continue");
            sc.nextLine();
            sc.nextLine();
            mainMenu();
        } else {
            System.out.println("Check out(Y/N):");
            sc.nextLine();
            String checkout = sc.nextLine();
            if (checkout.equals("Y")) {
                System.out.println("Discount:");
                int discount = sc.nextInt();
                new Query().updateCheckOut(book_id, reservation.getNo_of_rooms(), reservation.getRoom_type());
                mainMenu();
            } else {
                mainMenu();
            }
        }
    }

    public void getBookings() {
        List<Reservation> reservations = new Query().getReservations();
        for (int i = 0; i < reservations.size(); i++) {
            String Guest = new Query().getGuest(reservations.get(i).getGuest_id());
            System.out.println("booking#    Customer name    Room Type   no of rooms    no of days");
            System.out.println("    " + i + "      " + "    " + Guest + "    " + reservations.get(i).getRoom_type() + "   " + "      " + reservations.get(i).getNo_of_rooms() + "      " + "    " + "      " + reservations.get(i).getNo_of_days());
        }
        System.out.print("person has checked out  enter any key to continue");
        sc.nextLine();
        sc.nextLine();
        mainMenu();
    }

    public void getRooms() {
        List<Room> room = new Query().getRooms(0, 1);
        for (int i = 0; i < room.size(); i++) {
            System.out.println("room id#     Room Type   no of rooms Availible");
            System.out.println("  " + room.get(i).getRoomId() + "           " + "" + room.get(i).getRoomType() + "          " + room.get(i).getNo_of_rooms());
        }
        System.out.print("person has checked out  enter any key to continue");
        sc.nextLine();
        sc.nextLine();
        mainMenu();
    }

    public void mainMenu() {

        Scanner sc = new Scanner(System.in);
        String choice;
        do {
            System.out.println("Hotel Management System");
            System.out.println("1. Book a Room");
            System.out.println("2. Bill Service");
            System.out.println("3. Current Bookings");
            System.out.println("4. Available Rooms");
            System.out.println("5. Exit");
            System.out.print("Select:");
            choice = sc.nextLine();
            switch (choice) {
                case "1":

                    bookRoom();
                    break;
                case "2":
                    billService();
                    break;
                case "3":
                    getBookings();
                    break;
                case "4":
                    getRooms();
                    break;
                case "5": {
                    try {
                        outStream.close();
                        outStream.close();
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(HotelManagement.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                break;

                default:
                    System.out.println("Invalid Choice!");
                    choice = "invalid";
                    break;
            }
        } while (choice.equals("invalid"));
    }
}

SOURCE CODE :

src/com/film/model/ Ticket.java
package com.film.model; public class Ticket {
private int ticketId;
private String movieName; private String showTime; private String seatNumber; private double price; public Ticket() {}
public Ticket(int ticketId, String movieName, String showTime, String seatNumber, double price) { this.ticketId = ticketId;
this.movieName = movieName; this.showTime = showTime; this.seatNumber = seatNumber; this.price = price;
}
public int getTicketId() { return ticketId; }
public void setTicketId(int ticketId) { this.ticketId = ticketId; } public String getMovieName() { return movieName; }
public void setMovieName(String movieName) { this.movieName = movieName; } public String getShowTime() { return showTime; }
public void setShowTime(String showTime) { this.showTime = showTime; } public String getSeatNumber() { return seatNumber; }
public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; } public double getPrice() { return price; }
public void setPrice(double price) { this.price = price; }
}

src/com/film/util/ DBConnection.java
package com.film.util; import java.sql.Connection;
import java.sql.DriverManager; public class DBConnection {
private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe"; private static final String USER = "system"; // change if needed
private static final String PASS = "system"; // change if needed public static Connection getConnection() throws Exception {
Class.forName("oracle.jdbc.driver.OracleDriver");
return DriverManager.getConnection(URL, USER, PASS);
}
}
 
src/com/film/dao/ TicketDAO.java
package com.film.dao;
import com.film.model.Ticket; import java.util.List;
public interface TicketDAO {
void addTicket(Ticket ticket) throws Exception; void updateTicket(Ticket ticket) throws Exception; void deleteTicket(int ticketId) throws Exception; Ticket getTicketById(int ticketId) throws Exception; List<Ticket> getAllTickets() throws Exception;
List<Ticket> searchTickets(String movieName) throws Exception;
}

TicketDAOImpl.java
package com.film.dao;
import com.film.model.Ticket; import com.film.util.DBConnection; import java.sql.*;
import java.util.ArrayList; import java.util.List;
public class TicketDAOImpl implements TicketDAO { @Override
public void addTicket(Ticket ticket) throws Exception {
String sql = "INSERT INTO tickets VALUES (?, ?, ?, ?, ?)"; try (Connection conn = DBConnection.getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1, ticket.getTicketId());
ps.setString(2, ticket.getMovieName()); ps.setString(3, ticket.getShowTime()); ps.setString(4, ticket.getSeatNumber()); ps.setDouble(5, ticket.getPrice()); ps.executeUpdate();
}
}
@Override
public void updateTicket(Ticket ticket) throws Exception {
String sql = "UPDATE tickets SET movie_name=?, show_time=?, seat_number=?, price=? WHERE ticket_id=?";
try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) { ps.setString(1, ticket.getMovieName());
ps.setString(2, ticket.getShowTime()); ps.setString(3, ticket.getSeatNumber()); ps.setDouble(4, ticket.getPrice()); ps.setInt(5, ticket.getTicketId()); ps.executeUpdate();
}
}
 
@Override
public void deleteTicket(int ticketId) throws Exception { String sql = "DELETE FROM tickets WHERE ticket_id=?"; try (Connection conn = DBConnection.getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1, ticketId);
ps.executeUpdate();
}
}
@Override
public Ticket getTicketById(int ticketId) throws Exception { String sql = "SELECT * FROM tickets WHERE ticket_id=?"; try (Connection conn = DBConnection.getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1, ticketId);
try (ResultSet rs = ps.executeQuery()) { if (rs.next()) {
return new Ticket( rs.getInt("ticket_id"), rs.getString("movie_name"), rs.getString("show_time"), rs.getString("seat_number"), rs.getDouble("price")
);
}
}
}
return null;
}
@Override
public List<Ticket> getAllTickets() throws Exception { List<Ticket> tickets = new ArrayList<>();
String sql = "SELECT * FROM tickets ORDER BY ticket_id"; try (Connection conn = DBConnection.getConnection();
Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) { while (rs.next()) {
tickets.add(new Ticket( rs.getInt("ticket_id"), rs.getString("movie_name"), rs.getString("show_time"), rs.getString("seat_number"), rs.getDouble("price")
));
}
}
return tickets;
}
@Override
public List<Ticket> searchTickets(String movieName) throws Exception { List<Ticket> tickets = new ArrayList<>();
 
String sql = "SELECT * FROM tickets WHERE LOWER(movie_name) LIKE ?"; try (Connection conn = DBConnection.getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) { ps.setString(1, "%" + movieName.toLowerCase() + "%"); try (ResultSet rs = ps.executeQuery()) {
while (rs.next()) { tickets.add(new Ticket(
rs.getInt("ticket_id"), rs.getString("movie_name"), rs.getString("show_time"), rs.getString("seat_number"), rs.getDouble("price")
));
}
}
}
return tickets;
}
}

src/com/film/servlet/ AddTicketServlet.java
package com.film.servlet;
import com.film.dao.TicketDAO; import com.film.dao.TicketDAOImpl; import com.film.model.Ticket;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*; import java.io.IOException; @WebServlet("/AddTicketServlet")
public class AddTicketServlet extends HttpServlet {
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
try {
Ticket ticket = new Ticket( Integer.parseInt(request.getParameter("ticket_id")), request.getParameter("movie_name"), request.getParameter("show_time"), request.getParameter("seat_number"), Double.parseDouble(request.getParameter("price"))
);
TicketDAO dao = new TicketDAOImpl(); dao.addTicket(ticket);
} catch (Exception e) { e.printStackTrace();
}
response.sendRedirect("ViewTicketServlet");
}}
 
ViewTicketServlet.java

package com.film.servlet;
import com.film.dao.TicketDAO; import com.film.dao.TicketDAOImpl; import com.film.model.Ticket;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*; import java.io.IOException; import java.util.List;
@WebServlet("/ViewTicketServlet")
public class ViewTicketServlet extends HttpServlet {
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
try {
TicketDAO dao = new TicketDAOImpl(); List<Ticket> tickets = dao.getAllTickets(); request.setAttribute("tickets", tickets);
RequestDispatcher rd = request.getRequestDispatcher("view_tickets.jsp"); rd.forward(request, response);
} catch (Exception e) { e.printStackTrace();
}
}
}
UpdateTicketServlet.java

package com.film.servlet;
import com.film.dao.TicketDAO; import com.film.dao.TicketDAOImpl; import com.film.model.Ticket;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*; import java.io.IOException; @WebServlet("/UpdateTicketServlet")
public class UpdateTicketServlet extends HttpServlet {
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
try {
Ticket ticket = new Ticket( Integer.parseInt(request.getParameter("ticket_id")), request.getParameter("movie_name"), request.getParameter("show_time"), request.getParameter("seat_number"), Double.parseDouble(request.getParameter("price"))
);
TicketDAO dao = new TicketDAOImpl(); dao.updateTicket(ticket);
 
} catch (Exception e) { e.printStackTrace();
}
response.sendRedirect("ViewTicketServlet");
}
}

DeleteTicketServlet.java
package com.film.servlet;
import com.film.dao.TicketDAO; import com.film.dao.TicketDAOImpl; import jakarta.servlet.*;
import jakarta.servlet.http.*; import jakarta.servlet.annotation.*; import java.io.IOException;
@WebServlet("/DeleteTicketServlet")
public class DeleteTicketServlet extends HttpServlet {
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
try {
int ticketId = Integer.parseInt(request.getParameter("ticket_id")); TicketDAO dao = new TicketDAOImpl(); dao.deleteTicket(ticketId);
} catch (Exception e) { e.printStackTrace();
}
response.sendRedirect("ViewTicketServlet");
}
}

WebContent/view/add_ticket.jsp

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Ticket</title>
<link rel="stylesheet" type="text/css" href="../css/styles.css">
</head>
<body>
<header>
<div class="logo">\//\ Film Booking </div>
</header>
<div class="form-section">
<h2>Add Movie Ticket</h2>
<form action="AddTicketServlet" method="post">
<input type="text" name="ticket_id" placeholder="Ticket ID" required>
<input type="text" name="movie_name" placeholder="Movie Name" required>
 
<input type="text" name="show_time" placeholder="Show Time">
<input type="text" name="seat_number" placeholder="Seat Number">
<input type="text" name="price" placeholder="Price" required>
<br>
<input type="submit" class="btn" value="Add Ticket">
</form>
<br>
<a href="view_tickets.jsp" class="btn">View Tickets</a>
</div>
<footer>
Film Ticket Booking | Designed by Latchaya
</footer>
</body>
</html>

WebContent/view/update_ticket.jsp

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update Ticket</title>
<link rel="stylesheet" type="text/css" href="../css/styles.css">
</head>
<body>
<header>
<div class="logo">\//\ Film Booking </div>
</header>
<div class="form-section">
<h2>Update Movie Ticket</h2>
<form action="UpdateTicketServlet" method="post">
<input type="text" name="ticket_id" placeholder="Ticket ID (Existing)" required>
<input type="text" name="movie_name" placeholder="Movie Name" required>
<input type="text" name="show_time" placeholder="Show Time">
<input type="text" name="seat_number" placeholder="Seat Number">
<input type="text" name="price" placeholder="Price" required>
<br>
<input type="submit" class="btn" value="Update Ticket">
</form>
<br>
<a href="view_tickets.jsp" class="btn">View Tickets</a>
</div>
<footer>
Film Ticket Booking | Designed by Latchaya
</footer>
</body>
</html>
 
WebContent/view/view_tickets.jsp

<%@ page import="java.util.*, com.film.model.Ticket" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>View Tickets</title>
<link rel="stylesheet" type="text/css" href="../css/styles.css">
</head>
<body>
<header>
<div class="logo">\//\ Film Booking </div>
</header>
<div class="table-container">
<h2 style="text-align:center; margin-bottom:20px;">All Movie Tickets</h2>
<table class="ticket-table">
<thead>
<tr>
<th>ID</th>
<th>Movie</th>
<th>Show Time</th>
<th>Seat</th>
<th>Price</th>
<th>Actions</th>
</tr>
</thead>
<tbody>
<%
List<Ticket> tickets = (List<Ticket>) request.getAttribute("tickets"); if (tickets != null && !tickets.isEmpty()) {
for (Ticket t : tickets) {
%>
<tr>
<td><%= t.getTicketId() %></td>
<td><%= t.getMovieName() %></td>
<td><%= t.getShowTime() %></td>
<td><%= t.getSeatNumber() %></td>
<td>₹ <%= t.getPrice() %></td>
<td>
<a href="update_ticket.jsp?ticket_id=<%=t.getTicketId()%>" class="btn">Update</a>
<a href="DeleteTicketServlet?ticket_id=<%=t.getTicketId()%>" class="btn" onclick="return confirm('Delete this ticket?')">Delete</a>
</td>
</tr>
<%
}
} else {
%>
<tr>
 
<td colspan="6">No tickets found.</td>
</tr>
<%
}
%>
</tbody>
</table>
<br>
<a href="add_ticket.jsp" class="btn">Add New Ticket</a>
</div>
<footer>
Film Ticket Booking | Designed by Latchaya
</footer>
</body>
</html>

WebContent/view/index.jsp

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Film Ticket Booking</title>
<link rel="stylesheet" type="text/css" href="../css/styles.css">
<script>
function goTo(action) { switch(action) {
case 'add': window.location.href = 'add_ticket.jsp'; break;
case 'view': window.location.href = 'ViewTicketServlet'; break; case 'update': window.location.href = 'update_ticket.jsp'; break;
}
}
</script>
</head>
<body>
<header>
<div class="logo">\//\ Film Booking </div>
</header>
<section class="hero">
<div>
<h1>Book Your Movie Tickets Instantly</h1>
<p>.. Experience cinema like never before ..</p>
<div>
<button class="btn" onclick="goTo('add')">Add Ticket</button>
<button class="btn" onclick="goTo('view')">View Tickets</button>
<button class="btn" onclick="goTo('update')">Update Ticket</button>
</div>
</div>
</section>
 
<!-- 🎟 Features -->
<section class="features">
<div class="card">
<h3>/\ Easy Booking /\</h3>
<p>Book tickets quickly with our simple interface !!</p>
</div>
<div class="card">
<h3>/\ Choose Seats /\</h3>
<p>Select your preferred seats comfortably !!</p>
</div>
<div class="card">
<h3>/\ Instant Confirmation /\</h3>
<p>Get immediate booking confirmation !!</p>
</div>
</section>
<footer>
Film Ticket Booking | Designed by Latchaya
</footer>
</body>
</html>

SQL QUERY

CREATE TABLE tickets (
ticket_id NUMBER PRIMARY KEY, movie_name VARCHAR2(100) NOT NULL, show_time VARCHAR2(50),
seat_number VARCHAR2(20), price NUMBER(10,2)
);
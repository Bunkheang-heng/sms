import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public void addUser(User user) {
        String query = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, "password"); // Use hashed password in a real app
            ps.setString(4, user.getRole());
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1));
            }
            System.out.println("User added successfully with ID: " + user.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllTeachers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = 'teacher'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("role"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void assignTeacherToCourse(int teacherId, int courseId) {
        String query = "INSERT INTO teacher_courses (teacher_id, course_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
            System.out.println("Assigned teacher ID " + teacherId + " to course ID " + courseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

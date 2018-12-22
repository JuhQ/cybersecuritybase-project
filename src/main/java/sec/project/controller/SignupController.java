package sec.project.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(Model model, @RequestParam String name, @RequestParam String address, @RequestParam String product) throws Exception {
        signupRepository.save(new Signup(name, address));
        model.addAttribute("name", name);
        model.addAttribute("address", address);
        model.addAttribute("product", product);
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");

            Statement st = conn.createStatement();
            st.execute("INSERT INTO orders (name, address) VALUES ('" + name + "', '" + address + "')");

            PreparedStatement statement = conn.prepareStatement("SELECT * FROM orders WHERE name=? AND address=?");
            statement.setString(1, name);
            statement.setString(1, address);
            statement.executeQuery();

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                model.addAttribute("id", id);
            }

//            PreparedStatement prepStatement = conn.prepareStatement("INSERT INTO orders (name, address) VALUES (?, ?)");
//
//            prepStatement.setString(1, name);
//            prepStatement.setString(2, address);
//            prepStatement.executeUpdate();
        } catch (SQLException e) {
            // ignore
        }

        return "done";
    }

    @RequestMapping(value = "/order/<id>", method = RequestMethod.GET)
    public String showOrder(Model model, @RequestParam String idParam) throws Exception {

        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");

            PreparedStatement statement = conn.prepareStatement("SELECT * FROM orders WHERE id=?");
            statement.setString(1, idParam);
            statement.executeQuery();

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                model.addAttribute("id", id);
                model.addAttribute("name", name);
                model.addAttribute("address", address);
            }

        } catch (SQLException e) {
            // ignore
        }

        return "order";
    }

}

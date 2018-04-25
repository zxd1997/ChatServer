import Beans.UserBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDao userDao=new UserDao();
        response.setContentType("text/plain");
        PrintWriter printWriter=response.getWriter();
        String type=request.getParameter("type");
        System.out.println(type);
        if (type.equals("Login")){
            String username=request.getParameter("username");
            String password=request.getParameter("password");
            UserBean user=userDao.getUserByName(username);
            System.out.println(username+" "+password+" "+user.getUsername()+" "+user.getPassword());
            if (username.equals(user.getUsername())&&password.equals(user.getPassword())){
                printWriter.print("true");
            }else printWriter.print("false");
            printWriter.close();
        }else if(type.equals("Register")){
            String username=request.getParameter("username");
            String password=request.getParameter("password");
            String email=request.getParameter("email");
            System.out.println(username+" "+password+" "+email);
            if (userDao.getUserByName(username)==null){
                UserBean userBean=new UserBean(username,password,"");
                System.out.println(userBean.getUsername()+" "+userBean.getPassword()+" "+userBean.getEmail()+" "+ userBean.getId());
                userDao.addUser(userBean);
                printWriter.print("true");
            }else {
                printWriter.print("Already exists!");
            }
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

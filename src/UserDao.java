
import java.util.List;


import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Iterator;

import Beans.UserBean;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class UserDao {
    private SessionFactory sessionFactory;

    public UserDao() {
        Configuration configuration = new Configuration()
                .addAnnotatedClass(UserBean.class)
                .setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver")
                .setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/chat")
                .setProperty("hibernate.connection.username", "root")
                .setProperty("hibernate.connection.password", "19970226")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect")
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.show_sql", "true");
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    @Transactional
    public void addUser(UserBean user) {
        Session session = sessionFactory.openSession();
        Transaction tc = session.beginTransaction();
        try {
            session.save(user);
            tc.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteUser(UserBean user) {
        // TODO 自动生成的方法存根
        Session session = sessionFactory.openSession();
        Transaction tc = session.beginTransaction();
        try {
            session.delete(user);
            tc.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Transactional
    public void updateUser(UserBean user) {
        // TODO 自动生成的方法存根
        Session session = sessionFactory.openSession();
        Transaction tc = session.beginTransaction();
        try {
            session.update(user);
            tc.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Transactional
    public List<UserBean> selectUser() {
        // TODO 自动生成的方法存根

        List<UserBean> users = new ArrayList<UserBean>();
        Session session = sessionFactory.openSession();
        try {
            List list = (List) session.createQuery("From UserBean").list();
            for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
                UserBean u = (UserBean) iterator.next();
                users.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return users;
    }

    @Transactional
    public UserBean getUserById(int id) {
        // TODO 自动生成的方法存根
        Session session = sessionFactory.openSession();
        UserBean user=null;
        try {
            user =session.get(UserBean.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return user;
    }

    @Transactional
    public boolean isExisted(String name) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Transactional
    public UserBean getUserByName(String name) {
        // TODO 自动生成的方法存根
        Session session = sessionFactory.openSession();
        try {
            List user = (List) session.createQuery("From UserBean u where u.username=:username").setString("username", name).list();
            if (user.size() > 0)
                for (Iterator iterator = user.iterator(); iterator.hasNext(); ) {
                    UserBean u = (UserBean) iterator.next();
                    return u;
                }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }
}

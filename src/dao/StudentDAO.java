package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import bean.StudentBean;

public class StudentDAO {
	DataSource ds;

	
	public StudentDAO() throws ClassNotFoundException{
		try{
			ds = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/EECS");
		} catch(NamingException e) {
				e.printStackTrace();
		}
	}
	
	
	
	public Map<String, StudentBean> retrieve(String namePrefix, int credit_taken) throws SQLException {
		String query = "select * from STUDENTS where SURNAME like '%" + namePrefix + "%' and credit_taken >= " + credit_taken;
		
		Map<String, StudentBean> rv= new HashMap<String, StudentBean>();
		Connection con = (Connection) this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		
		ResultSet r = p.executeQuery();
		
		while(r.next()){
			String name = r.getString("GIVENNAME") + ", "+ r.getString("SURNAME");
			String sid = r.getString("SID");
			int creditTaken = r.getInt("CREDIT_TAKEN");
			int credit_graduate = r.getInt("CREDIT_GRADUATE");
			
			StudentBean s = new StudentBean(sid, name, creditTaken ,credit_graduate);
			
			//System.out.println(sid + " " + name + " " + creditTaken + " " + credit_graduate);
			
			rv.put(sid, s);
		}
		r.close();
		p.close();
		con.close();
		return rv;
		}
	
	public int insert(String sid, String givenName, String surname, int creditTaken, int creditGraduate)throws SQLException, NamingException {
		String query = "INSERT INTO STUDENTS(SID,GIVENNAME,SURNAME,CREDIT_TAKEN,CREDIT_GRADUATE) VALUES (?, ?, ?, ?, ?)";
		
		Connection con = (Connection) this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		
		p.setString(1, sid);
		p.setString(2, givenName);
		p.setString(3, surname);
		p.setInt(4, creditTaken);
		p.setInt(5, creditGraduate);
		
		con.close();
		
		return p.executeUpdate();
	}
	
	public int delete(String sid)throws SQLException, NamingException {
		String query = "DELETE FROM STUDENTS WHERE SID=?";
		
		Connection con = (Connection) this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		
		p.setString(1, sid);
		
		con.close();
		
		return p.executeUpdate();
	}
	
}

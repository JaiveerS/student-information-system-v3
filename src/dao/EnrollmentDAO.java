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

import bean.EnrollmentBean;

public class EnrollmentDAO {
	DataSource ds;
	
	public EnrollmentDAO() throws ClassNotFoundException{
		try{
			ds = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/EECS");
		} catch(NamingException e) {
				e.printStackTrace();
		}
	}
	
	
	public Map<String, EnrollmentBean> retrieve(String cid) throws SQLException {
		String query = "select * from enrollment where cid like " + "'%" + cid + "%'";
		
		Map<String, EnrollmentBean> rv= new HashMap<String, EnrollmentBean>();
		Connection con = (Connection) this.ds.getConnection();
		PreparedStatement p = ((java.sql.Connection) con).prepareStatement(query);
		
		ResultSet r = p.executeQuery();
		
		while(r.next()){
			String sid = r.getString("SID");
			int credit = r.getInt("CREDIT");
			
			EnrollmentBean s = new EnrollmentBean(cid, sid, credit);
			
			rv.put(cid, s);
		}
		r.close();
		p.close();
		con.close();
		return rv;
		}
}

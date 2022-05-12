package rest;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import model.SIS;

@Path("student")
public class Student {
	
	
	//return just name of student? 
	//http://localhost:8080/SIS-v3/rest/student/read?studentName=S
	@GET
	@Path("/read/")
	@Produces("text/plain")
	public String getStudent(@QueryParam("studentName")String name) throws Exception
	{
		SIS instance = SIS.getInstance();
		String n = instance.exportStudentXML(name, "0");
		return n;
	}
	
	
	//create a student with the given info and add to db
	//http://localhost:8080/SIS-v3/rest/student/create?sid=cse12345&givenName=Jaiveer&surName=Singh&creditTaken=118&creditGraduate=120
	@POST
	@Path("/create/")
	@Consumes("text/plain")
	@Produces("text/plain")
	public String createStudent(@QueryParam("sid")String sid, @QueryParam("givenName")String givenName, @QueryParam("surName")String surname, 
			@QueryParam("creditTaken")String creditTaken,@QueryParam("creditGraduate")String creditGraduate) throws ClassNotFoundException, SQLException, NamingException
			{
				SIS instance = SIS.getInstance();
				String n = instance.insertStudent(sid, givenName, surname, creditTaken, creditGraduate);
				System.out.println(n);
				return "insertedRows: " + n;
			}
	
	
	//delete the student with the given sid from the db
	//http://localhost:8080/SIS-v3/rest/student/delete?sid=cse12345
	@DELETE
	@Path("/delete/")
	@Consumes("text/plain")
	@Produces("text/plain")
	public String delete(@QueryParam("sid")String sid) throws ClassNotFoundException, SQLException, NamingException {
		SIS instance = SIS.getInstance();
		String n = instance.deleteStudent(sid);
		return "deletedRows: " + n;
	}
	
}

package model;

import java.io.File;
import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.naming.NamingException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import bean.EnrollmentBean;
import bean.ListWrapperBean;
import bean.StudentBean;
import ctrl.Sis;
import dao.EnrollmentDAO;
import dao.StudentDAO;

public class SIS {
	private StudentDAO student;
	private EnrollmentDAO enrollment;
	private static SIS instance;
	
	
	//ensures only one instance of SIS model
	public static SIS getInstance() throws ClassNotFoundException {
		if(instance == null) {
			instance = new SIS();
		}
		return instance;
	}
	
	private SIS() {
		try {
			student = new StudentDAO();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			enrollment = new EnrollmentDAO();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	
	//Retrieves Students from DB with given namePrefix & given credit_taken
	public Map<String, StudentBean> retriveStudent(String namePrefix, String credit_taken) throws Exception{
		String namePrefixSanitized = sanitize(namePrefix);
		String creditTakenSanitized = sanitize(credit_taken);
		
		if(validateSurname(namePrefixSanitized) && validateCreditTaken(creditTakenSanitized)) {
			int taken = Integer.parseInt(creditTakenSanitized);
			return student.retrieve(namePrefixSanitized, taken);
		}else {
			Map<String, StudentBean> student = new HashMap<String, StudentBean>();
			return student;
		}
	}
	
	
	//Used to insert a student into the DB
	public String insertStudent(String sid, String givenName, String surname, String creditTaken, String creditGraduate) throws NamingException, SQLException {
		String studentID = sanitize(sid);
		String firstName = sanitize(givenName);
		String lastName = sanitize(surname);
		String credit_taken = sanitize(creditTaken);
		String credit_graduate = sanitize(creditGraduate);
	
		//System.out.println("sanitized"); //for debugging
		
		if(validateSID(studentID) && validateGivenName(firstName) && validateSurname(lastName) && validateCreditTaken(credit_taken) && validateCreditGraduate(credit_graduate)) {
			int creditTakenInt = Integer.parseInt(credit_taken);
			int creditGraduateInt = Integer.parseInt(credit_graduate);
			
			int result;
			try {
				result = student.insert(studentID, firstName, lastName, creditTakenInt, creditGraduateInt);
				return String.valueOf(result);
			} catch (SQLIntegrityConstraintViolationException e) {
				return "0 : (entry with same SID already exists)";
			} 
		}else {
			return "0 : (Invalid Inputs)";
		}
	}
	
	
	
	//delete a student with the given sid from DB
	public String deleteStudent(String sid) throws SQLException, NamingException {
		String studentID = sanitize(sid);
		
		if(validateSID(studentID)) {
			int result = student.delete(studentID);
			return String.valueOf(result);
		}else {
			return "0 : (Invalid Input)";
		}
	}
	
	
	
	
	//returns students with given namePrefix & credit_taken in XML 
	public String exportStudentXML(String namePrefix, String credit_taken) throws Exception{
		String namePrefixSanitized = sanitize(namePrefix);
		String creditTakenSanitized = sanitize(credit_taken);
		
		Map<String, StudentBean> r = retriveStudent(namePrefixSanitized, creditTakenSanitized);
		List<StudentBean> studentList = new ArrayList<StudentBean>();
		
		for (Map.Entry<String, StudentBean> entry :r.entrySet()) {
			studentList.add(entry.getValue());
		}
		
		int taken = Integer.parseInt(creditTakenSanitized);
		ListWrapperBean lw = new ListWrapperBean(namePrefixSanitized, taken, studentList);
		
		JAXBContext jc = JAXBContext.newInstance(lw.getClass());
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		
//Uses XML Schema but need to run web app first creates dependency on SIS or can type absolute path of xsd file where Sis.getXsdTarget() is written to avoid dependency
//		SchemaFactory sf = 	
//				SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//		Schema schema = sf.newSchema(new File(Sis.getXsdTarget()));
//		marshaller.setSchema(schema);
		
		StringWriter sw = new StringWriter();
		sw.write("\n");
		marshaller.marshal(lw, new StreamResult(sw));
		
		//System.out.println(sw.toString()); // for debugging	
		return sw.toString();
	}
	
	
	
	
	//returns students with given namePrefix & credit_taken in JSON
	public String exportStudentJSON(String namePrefix, String credit_taken) throws Exception {
		String namePrefixSanitized = sanitize(namePrefix);
		String creditTakenSanitized = sanitize(credit_taken);
		
		Map<String, StudentBean> studentInfo = retriveStudent(namePrefixSanitized, creditTakenSanitized);
		
		JsonArrayBuilder jArray = Json.createArrayBuilder();
		for (Map.Entry<String, StudentBean> entry :studentInfo.entrySet()) {
			JsonObject valueOBJ = Json.createObjectBuilder().add("sid", entry.getValue().getSid()).add("name", entry.getValue().getName())
					.add("credit_taken", entry.getValue().getCredit_taken()).add("credit_graduate", entry.getValue().getCredit_graduate()).build();
			
			jArray.add(valueOBJ);
		}
		
		JsonArray jsonArray = jArray.build();
		return jsonArray.toString();
	}
	
	
	//Used to retrieve enrollment information from a given student
	public Map<String, EnrollmentBean> retriveEnrollment(String cid) throws Exception{
		
		if(validateCID(cid)) {
			String classid = sanitize(cid);
			return enrollment.retrieve(classid);
		}else {
			Map<String, EnrollmentBean> classes = new HashMap<String, EnrollmentBean>();
			return classes;
		}
	}

	

	//sanitizes string by removing all non-alphanumeric characters
	private static String sanitize(String s) {
		return s.replaceAll("[\\W+]", "");
	}
	
	
	//checks if SID is comprised on alphanumberic characters only
	public boolean validateSID(String sid) {
		return sid.matches("^[a-zA-Z0-9]*$");
	}
	
	//checks if CID is comprised on alphanumberic characters only
	public boolean validateCID(String cid) {
		return cid.matches("^[a-zA-Z0-9]*$");
	}
	
	//checks if firstName is comprised of letters only
	public boolean validateGivenName(String givenName) {
		return givenName.matches("[A-Z][a-z]*");
	}
	
	//checks if lastName is comprised of letters only
	public boolean validateSurname(String surname) {
		return surname.matches("[A-Z][a-z]*");
	}
	
	//checks if creditTaken is a number between 0 & 150
	public boolean validateCreditTaken(String creditTaken) {
		boolean result = true;
		try {
            int num = Integer.parseInt(creditTaken);            
    		if(num < 0 || num > 150) {
    			result = false;
    		}
        } catch (NumberFormatException e) {
            result = false;
        }
		return result;
	}
	
	//checks if creditGraduate is a number between 0 & 150
	public boolean validateCreditGraduate(String creditGraduate) {
		boolean result = true;
		try {
            int num = Integer.parseInt(creditGraduate);
    		if(num < 0 || num > 150) {
    			result = false;
    		}
        } catch (NumberFormatException e) {
            result = false;
        }
		return result;
	}

}

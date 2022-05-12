package ctrl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.StudentBean;
import model.SIS;

/**
 * Servlet implementation class Sis
 */
@WebServlet("/Sis")
public class Sis extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String target = "/Form.jspx";
	
	private String XMLfile = "SIS.xml";
	//where xml file will be written and read from set in init
	private String XMLtarget = "";
	
	private String JSONfile = "SIS.json";
	//where JSON file will be written and read from set in init
	private String JSONtarget =  "";
	
//	private String XSDfile = "SIS.xsd";
//	private static String XSDtarget = "";
	private ServletContext context;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Sis() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		context = getServletContext();
		
		SIS model;
		try {
			model = SIS.getInstance();
			context.setAttribute("model", model);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//set where xml file will be written to
		XMLtarget = this.getServletContext().getRealPath(XMLfile);
		//where JSON will be written to
		JSONtarget =  this.getServletContext().getRealPath(JSONfile);
//		//set where XSD file will be read from
//		XSDtarget =  this.getServletContext().getRealPath(XSDfile);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		
		//get inputs from request and store as local variables
		String namePrefix = request.getParameter("namePrefix");
		String credit_taken = request.getParameter("minCreditTaken");
		
		//get instance of model
		SIS model = (SIS) context.getAttribute("model");
		
		if (request.getParameter("report") != null && request.getParameter("report").equals("true")){//do this when report button is clicked
			System.out.println("CLICKED REPORT BUTTON");
			
			//make map to store students retrieved
			Map<String, StudentBean> studentInfo = null;
			try {
				//retrieve student info and store in map
				studentInfo = model.retriveStudent(namePrefix, credit_taken);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//count number of entries in table
			int numOfEntries = 0;
			//initalizes table to be returned to client
			String table = "<table><tr> <th>Id</th> <th>Name</th> <th>Credits Taken</th> <th>Credits to graduate</th> </tr>";
			
			//iterate over map and make table
			for (Map.Entry<String, StudentBean> entry :studentInfo.entrySet()) {
				table = table.concat("<tr> ");
				table = table.concat("<td>" + entry.getValue().getSid() + "</td>");
				table = table.concat("<td>" + entry.getValue().getName() + "</td>");
				table = table.concat("<td>" + entry.getValue().getCredit_taken() + "</td>");
				table = table.concat("<td>" + entry.getValue().getCredit_graduate() + "</td>");
				table = table.concat(" </tr>");
				numOfEntries++;
			}
	
			table = table.concat("</table>");
			String entries = "There are " + numOfEntries + " entries.";
			
			//set return values for response to client
			request.setAttribute("entries", entries);
			request.setAttribute("table", table);
			
			//show client the target page
			request.getRequestDispatcher(target).forward(request, response);
			
			
		}else if(request.getParameter("xml") != null && request.getParameter("xml").equals("true")){ //do this when Generate XML button is clicked
			
			System.out.println("clicked XML BUTTON");
			String XmlReturned = "";
			
			//get db info and return as xml for given namePrefix & credit_taken
			try {
				XmlReturned = model.exportStudentXML(namePrefix, credit_taken);
				//create a file at XMLtarget location
				FileWriter fw = new FileWriter(XMLtarget);
				
				//write xml to file specified in fw
				fw.write(XmlReturned);
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println(XmlReturned); //for debugging
			
			//redirect page to XMLfile 
			response.sendRedirect(XMLfile);
		}else if(request.getParameter("json") != null && request.getParameter("json").equals("true")) { //do this when either report JSON button is clicked
			System.out.println("CLICKED JSON BUTTON");
			String jsonResult = "";

			//get db info and return as JSON for given namePrefix & credit_taken
			try {
				jsonResult = model.exportStudentJSON(namePrefix, credit_taken);
				FileWriter fw = new FileWriter(JSONtarget);
				//write JSON to file specified at location JSONtarget
				fw.write(jsonResult);
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//show client the content in JSONfile
			response.sendRedirect(JSONfile);
		}else {
			//show starting page when none of the above cases are satisfied
			request.getRequestDispatcher(target).forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
//	//used in model in export to locate xsd schema
//	public static String getXsdTarget() {
//		return XSDtarget;
//	}

}

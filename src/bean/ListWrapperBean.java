package bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="sisReport")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListWrapperBean {
	
	@XmlAttribute
	private String namePrefix;
	@XmlAttribute(name="creditTaken")
	private int credit_taken;
	@XmlElement(name="studentList")
	private List<StudentBean> list;
	
	public ListWrapperBean(String namePrefix, int credit_taken, List<StudentBean> list) {
		super();
		this.namePrefix = namePrefix;
		this.credit_taken = credit_taken;
		this.list = list;
	}
	
	public ListWrapperBean() {
		this.list = new ArrayList<StudentBean>();
	}

	public String getNamePrefix() {
		return namePrefix;
	}

	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}

	public int getCredit_taken() {
		return credit_taken;
	}

	public void setCredit_taken(int credit_taken) {
		this.credit_taken = credit_taken;
	}

	public List<StudentBean> getList() {
		return list;
	}

	public void setList(List<StudentBean> list) {
		this.list = list;
	}
	
	
}

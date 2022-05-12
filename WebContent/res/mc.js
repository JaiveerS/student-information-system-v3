function validate(){
	var ok = true;
	
	var check = /^[A-Za-z]+$/;
	var p= document.getElementById("namePrefix").value;
	
	if(p == "" || !check.test(p) || p[0] != p[0].toUpperCase()) {
		alert("Name Prefix is invalid must contain only letters and Prefix must begin with a capital letter!");
		ok = false;
	}
	
	if(!ok){
		return false;
	}
	
	var p = document.getElementById("minCreditTaken").value;
	if(isNaN(p) || p < 0 || p > 150) {
		alert("Minimum Credits must be between 0 and 150");
		ok = false;
	}
	
	return ok;
}

function doSimpleAjax(address){
	var request = new XMLHttpRequest();
	var data='';
	
	//grabs all parameters from form
	data += "json=true&";
	data += "namePrefix=" + document.getElementById("namePrefix").value + "&";
	data += "minCreditTaken=" + document.getElementById("minCreditTaken").value;
	console.log(data);
	
	
	request.onreadystatechange = function(){
		handler(request)
	}
	
	request.open("GET", (address + "?" + data), true);
	request.send(null);
}

function handler(request){
	if((request.readyState == 4) && (request.status == 200)){
		var target = document.getElementById("ajaxTarget");
		target.innerHTML = request.responseText;
	}else{
		var target = document.getElementById("ajaxTarget");
		target.innerHTML = "<STRONG> Waiting for response</STRONG>";
	}
}
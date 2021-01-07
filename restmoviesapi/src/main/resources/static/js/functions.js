"use strict";

var xmlhttp = new XMLHttpRequest();
var JSONArray;

function fetchData() {
	var y = document.getElementById("input");
	var input = y.value;
	input = input.trim().replace(" ", "+");
	var url = "http://www.omdbapi.com/?t="+input+"&apikey=51f1a1fd";
		xmlhttp.onreadystatechange = function() {
  		if (this.readyState == 4 && this.status == 200) {
      		JSONArray = JSON.parse(this.responseText);
        	var gen_info = document.getElementById("general_info");
            var image = document.getElementById("poster");
            var plot = document.getElementById("plot");
			if(JSONArray.Title!=undefined) {
				document.getElementById("save").value = "Save "+JSONArray.Title;
				gen_info.innerHTML = "Title: "+JSONArray.Title+"<br>Release date: "
				+JSONArray.Released+"<br>Genre: "+JSONArray.Genre;
				plot.innerHTML = JSONArray.Plot;
				image.setAttribute("src", JSONArray.Poster);
    		} else {
    			gen_info.innerHTML = "Movie not found!";
	   			plot.innerHTML = "";
				image.setAttribute("src", null);
			}					
   		}
	};
	xmlhttp.open("GET", url, true);
	xmlhttp.send();
}

function fetchFullPlot() {
	var y = document.getElementById("input");
	var input = y.value;
	input = input.trim().replace(" ", "+");
	var url = "http://www.omdbapi.com/?t="+input+"&apikey=51f1a1fd&plot=full";
		xmlhttp.onreadystatechange = function() {
  			if (this.readyState == 4 && this.status == 200) {
      			var fullplotJSONArray = JSON.parse(this.responseText);
                var plot = document.getElementById("plot");
			if(fullplotJSONArray.Title!=undefined) {
				plot.innerHTML = fullplotJSONArray.Plot;
    		} else {
	   			plot.innerHTML = "";
			}					
   		}
	};
	xmlhttp.open("GET", url, true);
	xmlhttp.send();
}

function getBookmarks(titlesFromBackend,emailFromBackend) {
//	console.log("Entered function getBookmarks");
//	console.log(titlesFromBackend);
	document.getElementById("personalisation").innerHTML = "Movies saved by "+emailFromBackend+":";
	var titleArray = titlesFromBackend.split(",");
    for(var i = 0; i < titleArray.length; i++) {
    	localStorage.setItem('title'+i,titleArray[i]);
    	
//    	console.log("Entered for");
		var wrapperDiv = document.createElement("DIV");
		document.getElementById("main").appendChild(wrapperDiv);
		wrapperDiv.setAttribute("id", "wrapper");
		
		var image = document.createElement("IMG");
		wrapperDiv.appendChild(image);
		image.setAttribute("id", "poster"+i);
		
		var infoDiv = document.createElement("DIV");
		wrapperDiv.appendChild(infoDiv);
		infoDiv.setAttribute("id", "verWrapper");
		
		var generalInfo = document.createElement("P");
		infoDiv.appendChild(generalInfo);
		generalInfo.setAttribute("id", "general_info"+i);
		generalInfo.setAttribute("onmouseover", "fetchBookmark("+i+");");
		generalInfo.innerHTML = titleArray[i];
		
		var plot = document.createElement("P");
		infoDiv.appendChild(plot);
		plot.setAttribute("id", "plot"+i);
    }
}

function fetchBookmark(i) {
//	console.log("i is: "+i);
	var title = localStorage.getItem('title'+i);
//	console.log("title is: "+title);
	if(document.getElementById("general_info"+i).innerHTML==title) {
		title = title.trim().replace(" ", "+");	
		var url = "http://www.omdbapi.com/?t="+title+"&apikey=51f1a1fd";
		xmlhttp.onreadystatechange = function() {
	  		if (this.readyState == 4 && this.status == 200) {
	      		JSONArray = JSON.parse(this.responseText);
	        	var gen_info = document.getElementById("general_info"+i);
	            var image = document.getElementById("poster"+i);
	            var plot = document.getElementById("plot"+i);
				if(JSONArray.Title!=undefined) {
					gen_info.innerHTML = "Title: "+JSONArray.Title+"<br>Release date: "
					+JSONArray.Released+"<br>Genre: "+JSONArray.Genre;
					plot.innerHTML = JSONArray.Plot;
					image.setAttribute("src", JSONArray.Poster);
					image.setAttribute("alt", "");
	    		} else {
	    			gen_info.innerHTML = "Movie not found!";
		   			plot.innerHTML = "";
					image.setAttribute("src", null);
				}					
	   		}
		};
		xmlhttp.open("GET", url, true);
		xmlhttp.send();
	}	
}

function errorMessage(messageFromBackend) {
	if(!messageFromBackend==" ") {
		var errorp = document.createElement("P");
		errorp.innerHTML = messageFromBackend;
		document.getElementById("main").appendChild(errorp);
	}
}

function personalise(emailFromBackend) {
	document.getElementById("personalisation").innerHTML = "Welcome "+emailFromBackend+", you can now search for movies!";
}

function deleteLocalStorage() {
	localStorage.clear();
//	console.log("local storage cleared");
}
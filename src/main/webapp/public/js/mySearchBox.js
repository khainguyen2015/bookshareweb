/* When the user clicks on the button, 
toggle between hiding and showing the dropdown content */


function buildDropDownMenu(items) {
	var itemsInJSON = JSON.parse(items);
	var text = "";
	document.getElementById("myDropdown").cl
	console.log(itemsInJSON);
	for (i = 0; i < itemsInJSON.length; i++) {
  		text += "\t<a class='dropdown-item' href='#'>" + itemsInJSON[i].bookName + "</a>" + "\n";
	}
	document.getElementById("myDropdown").innerHTML = text;
	if(text.length === 0) {
		document.getElementById("myDropdown").classList.toggle("show", false);
	} else {
		document.getElementById("myDropdown").classList.toggle("show", true);
	}
}

function loadSearchResults(searchKey, handleResponseFunction) {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      handleResponseFunction(this.response);
    }
  };
  xhttp.open("GET", "http://localhost:8080/BookShareWeb/api/v1/book/searchBook?search_key=" + searchKey, true);
  xhttp.send();
}

function showSearchSuggestionDropdown(searchKey) {
	if(searchKey === "") {
		console.log('close-dropdown');
    	document.getElementById("myDropdown").classList.toggle("show", false);
        return;
    }
    loadSearchResults(searchKey, buildDropDownMenu);
}

function search() {
	const searchKey = document.getElementById("nav-search-bar").value;
	window.location.href = "http://localhost:8080/BookShareWeb/result?search_key=" + searchKey;
}

// Close the dropdown if the user clicks outside of it
window.onclick = function(event) {
  if (!event.target.matches('#nav-search-bar')) {
  	if(document.getElementById("myDropdown") !== null) {
  		document.getElementById("myDropdown").classList.toggle("show", false);
  	}
  }
}
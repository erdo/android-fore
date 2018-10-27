function openLanguage(language) {
    // Declare all variables
    var i, tabcontent, tablinks

    // Get all elements with class="tabcontent" and hide or show them as appropriate
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
      if (tabcontent[i].classList.contains(language)) {
        tabcontent[i].style.display = "block";
      } else {
        tabcontent[i].style.display = "none";
      }
    }

    // Get all elements with class="tablinks" and remove or set the class "active"
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
      tablinks[i].className = tablinks[i].className.replace(" active", "");
      if (tabcontent[i].classList.contains(language)){
        tablinks[i].className += " active";
      }
    }
}

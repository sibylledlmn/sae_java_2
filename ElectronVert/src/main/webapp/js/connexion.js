document.addEventListener("DOMContentLoaded", function() {

    const params = new URLSearchParams(window.location.search);
    const msgErreur = document.getElementById("message-erreur");
    if(params.get("erreur") === "1"){
        msgErreur.style.display = "block"
    }


})
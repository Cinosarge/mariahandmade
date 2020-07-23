/*
 * Homepage slideshow
 */

/* Immagine corrente */
var i = 0;
/* Numero delle immagini nello slideshow */
var numImg = 5;
/* Millisecondi tra le diapositive */
var ms = 8000;
/* Indentificatore timer */
var timeoutId;

function prevImg(){
	clearTimeout(timeoutId);
	if(i == 0){
		i = numImg - 1;
	}
	else{
		--i;
	}
	document.getElementById('sliding-img').src = "/static/images/slideshow/" + i + ".png";
	timeoutId = setTimeout("nextImg()", ms);
}

function nextImg(){
	clearTimeout(timeoutId);
	i = ++i % numImg;
	document.getElementById('sliding-img').src = "/static/images/slideshow/" + i + ".png";
	timeoutId = setTimeout("nextImg()", ms);
}

/* Slideshow autostart */
timeoutId = setTimeout("nextImg()", ms);
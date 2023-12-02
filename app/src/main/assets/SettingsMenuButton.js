const halfCircleButton = document.createElement('div');
const svgCogwheel = document.createElementNS("http://www.w3.org/2000/svg", "svg");
const cogPath = document.createElementNS("http://www.w3.org/2000/svg", "path");

halfCircleButton.classList.add('half-circle-btn');

halfCircleButton.style.width = '100px';
halfCircleButton.style.height = '50px';
halfCircleButton.style.borderRadius = '50px 0 0 50px';
halfCircleButton.style.backgroundColor = '#8A2BE2';
halfCircleButton.style.display = 'flex';
halfCircleButton.style.alignItems = 'center';
halfCircleButton.style.justifyContent = 'center';
halfCircleButton.style.boxShadow = '0px 0px 5px rgba(0, 0, 0, 0.3)';
halfCircleButton.style.position = 'absolute';
halfCircleButton.style.left = '0';
halfCircleButton.style.top = '50%';
halfCircleButton.style.transform = 'translateY(-50%)';
halfCircleButton.style.cursor = 'pointer';

svgCogwheel.setAttribute('viewBox', '0 0 24 24');
svgCogwheel.setAttribute('width', '24');
svgCogwheel.setAttribute('height', '24');
svgCogwheel.setAttribute('fill', 'none');
svgCogwheel.setAttribute('stroke', '#fff');
svgCogwheel.setAttribute('stroke-width', '2');
svgCogwheel.setAttribute('stroke-linecap', 'round');
svgCogwheel.setAttribute('stroke-linejoin', 'round');

cogPath.setAttribute('d', 'M21 17a2 2 0 1 1-2-2 2 2 0 0 1 2 2zM7 10a2 2 0 1 1-2-2 2 2 0 0 1 2 2zM21 7a2 2 0 1 1-2-2 2 2 0 0 1 2 2zM12 3a2 2 0 1 1-2-2 2 2 0 0 1 2 2zM12 21a2 2 0 1 1-2-2 2 2 0 0 1 2 2z');

svgCogwheel.appendChild(cogPath);

const icon = document.createElement('div');
icon.innerHTML = '<svg aria-label="Options" fill="currentColor" height="24" role="img" viewBox="0 0 24 24" width="24"><title>Options</title><circle cx="12" cy="12" fill="none" r="8.635" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"></circle><path d="M14.232 3.656a1.269 1.269 0 0 1-.796-.66L12.93 2h-1.86l-.505.996a1.269 1.269 0 0 1-.796.66m-.001 16.688a1.269 1.269 0 0 1 .796.66l.505.996h1.862l.505-.996a1.269 1.269 0 0 1 .796-.66M3.656 9.768a1.269 1.269 0 0 1-.66.796L2 11.07v1.862l.996.505a1.269 1.269 0 0 1 .66.796m16.688-.001a1.269 1.269 0 0 1 .66-.796L22 12.93v-1.86l-.996-.505a1.269 1.269 0 0 1-.66-.796M7.678 4.522a1.269 1.269 0 0 1-1.03.096l-1.06-.348L4.27 5.587l.348 1.062a1.269 1.269 0 0 1-.096 1.03m11.8 11.799a1.269 1.269 0 0 1 1.03-.096l1.06.348 1.318-1.317-.348-1.062a1.269 1.269 0 0 1 .096-1.03m-14.956.001a1.269 1.269 0 0 1 .096 1.03l-.348 1.06 1.317 1.318 1.062-.348a1.269 1.269 0 0 1 1.03.096m11.799-11.8a1.269 1.269 0 0 1-.096-1.03l.348-1.06-1.317-1.318-1.062.348a1.269 1.269 0 0 1-1.03-.096" fill="none" stroke="currentColor" stroke-linejoin="round" stroke-width="2"></path></svg><svg aria-label="Options" class="x1lliihq x1n2onr6 x5n08af" fill="currentColor" height="24" role="img" viewBox="0 0 24 24" width="24"><title>Options</title><circle cx="12" cy="12" fill="none" r="8.635" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"></circle><path d="M14.232 3.656a1.269 1.269 0 0 1-.796-.66L12.93 2h-1.86l-.505.996a1.269 1.269 0 0 1-.796.66m-.001 16.688a1.269 1.269 0 0 1 .796.66l.505.996h1.862l.505-.996a1.269 1.269 0 0 1 .796-.66M3.656 9.768a1.269 1.269 0 0 1-.66.796L2 11.07v1.862l.996.505a1.269 1.269 0 0 1 .66.796m16.688-.001a1.269 1.269 0 0 1 .66-.796L22 12.93v-1.86l-.996-.505a1.269 1.269 0 0 1-.66-.796M7.678 4.522a1.269 1.269 0 0 1-1.03.096l-1.06-.348L4.27 5.587l.348 1.062a1.269 1.269 0 0 1-.096 1.03m11.8 11.799a1.269 1.269 0 0 1 1.03-.096l1.06.348 1.318-1.317-.348-1.062a1.269 1.269 0 0 1 .096-1.03m-14.956.001a1.269 1.269 0 0 1 .096 1.03l-.348 1.06 1.317 1.318 1.062-.348a1.269 1.269 0 0 1 1.03.096m11.799-11.8a1.269 1.269 0 0 1-.096-1.03l.348-1.06-1.317-1.318-1.062.348a1.269 1.269 0 0 1-1.03-.096" fill="none" stroke="currentColor" stroke-linejoin="round" stroke-width="2"></path></svg>';

halfCircleButton.appendChild(icon);

document.body.appendChild(halfCircleButton);
import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

// Load SockJS and Stomp BEFORE Angular bootstraps
const sockjsScript = document.createElement('script');
sockjsScript.src = 'https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js';
document.head.appendChild(sockjsScript);

const stompScript = document.createElement('script');
stompScript.src = 'https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js';
document.head.appendChild(stompScript);

// Wait for scripts to load, then bootstrap
stompScript.onload = () => {
  bootstrapApplication(App, appConfig)
    .catch((err) => console.error(err));
};
"use strict";var precacheConfig=[["/asaf-project/pres1android/index.html","4758e2f01c7c047889c3aa6c2b90bae4"],["/asaf-project/pres1android/static/css/main.653da357.css","521def3ae35e5b12d25e0300c45174f2"],["/asaf-project/pres1android/static/js/main.96df66c0.js","c244420c70365cc50e2539f186216a0c"],["/asaf-project/pres1android/static/media/android_lifecycle.5a4b7838.png","5a4b78388f48d86393c7a6a973f8a4da"],["/asaf-project/pres1android/static/media/arch_activities.b68e3a67.png","b68e3a679d3b4f60bc670630dcba62e5"],["/asaf-project/pres1android/static/media/arch_activity_rotation_1.09cd3412.png","09cd3412f35e979c98676cb9290919d6"],["/asaf-project/pres1android/static/media/arch_activity_rotation_2.56922e5f.png","56922e5f656823534670c401762028bc"],["/asaf-project/pres1android/static/media/arch_activity_rotation_3.96d7679a.png","96d7679a8f7f9221254340af2d4a1b71"],["/asaf-project/pres1android/static/media/arch_create_activities.29327ce6.png","29327ce687da63224030fefcb6f53eba"],["/asaf-project/pres1android/static/media/arch_create_fragments.bb15ce5c.png","bb15ce5c7da5fe6fe530f20a6909d35f"],["/asaf-project/pres1android/static/media/arch_fragment_communication.c09048c8.png","c09048c80895fda1b65ce33767db0480"],["/asaf-project/pres1android/static/media/arch_fragments.aed07a36.png","aed07a366e74af39bee223eff08f1c55"],["/asaf-project/pres1android/static/media/arch_radio.721d5894.png","721d58944ee3d0350f6f3bcaec77e7ff"],["/asaf-project/pres1android/static/media/arch_response_activities.1c9e5986.png","1c9e5986f493c68a2ee3e1439f7c96fa"],["/asaf-project/pres1android/static/media/arch_viewboundary_0.a837c57b.png","a837c57b70d3332f4518e200f84dbe3b"],["/asaf-project/pres1android/static/media/arch_viewboundary_1.32b3dee7.png","32b3dee7984186f476c6295aac13a10f"],["/asaf-project/pres1android/static/media/arch_viewboundary_2.d4277ff6.png","d4277ff6467c34e2dfbf5db1ae7567d8"],["/asaf-project/pres1android/static/media/arch_views.ac4e6632.png","ac4e6632e780ffd9f0d1e2cb90bc0998"],["/asaf-project/pres1android/static/media/camerafragment_0.f46f77f2.png","f46f77f2bc9346825a12e7c4e2e990be"],["/asaf-project/pres1android/static/media/camerafragment_1_cameralogic.e09d18c6.png","e09d18c6afbeb1b815ac0f6857ae0c2c"],["/asaf-project/pres1android/static/media/camerafragment_2_storingimagedata.bf678a84.png","bf678a84c710ad48db4ab1e379feeda3"],["/asaf-project/pres1android/static/media/camerafragment_3_threadmgt.ba28548a.png","ba28548abeed23b9444882eb6059869b"],["/asaf-project/pres1android/static/media/camerafragment_4_popupdialogs.caa4b1e4.png","caa4b1e47ea1bb90060b380bdd1480fc"],["/asaf-project/pres1android/static/media/camerafragment_5_mngpermissions.7a11d6de.png","7a11d6de53be3bc8d8642f04d6611651"],["/asaf-project/pres1android/static/media/camerafragment_6_androidlifecycle.aba9d98a.png","aba9d98a5bf8b807f550662c43c6372e"],["/asaf-project/pres1android/static/media/camerafragment_7_constantsstateflagslocalvariables.96c19a91.png","96c19a91c81013f2e3d84d0a41a86290"],["/asaf-project/pres1android/static/media/horizontal_separation.dd559ad4.png","dd559ad4750b67410b496215280b8aba"],["/asaf-project/pres1android/static/media/skull.a0785cc3.jpg","a0785cc356a20cab1567ea088b245ba1"],["/asaf-project/pres1android/static/media/vertical_separation.83af4392.png","83af4392a9d79566b6fd3c7cc3bb14c8"]],cacheName="sw-precache-v3-sw-precache-webpack-plugin-"+(self.registration?self.registration.scope:""),ignoreUrlParametersMatching=[/^utm_/],addDirectoryIndex=function(e,a){var t=new URL(e);return"/"===t.pathname.slice(-1)&&(t.pathname+=a),t.toString()},cleanResponse=function(a){return a.redirected?("body"in a?Promise.resolve(a.body):a.blob()).then(function(e){return new Response(e,{headers:a.headers,status:a.status,statusText:a.statusText})}):Promise.resolve(a)},createCacheKey=function(e,a,t,r){var c=new URL(e);return r&&c.pathname.match(r)||(c.search+=(c.search?"&":"")+encodeURIComponent(a)+"="+encodeURIComponent(t)),c.toString()},isPathWhitelisted=function(e,a){if(0===e.length)return!0;var t=new URL(a).pathname;return e.some(function(e){return t.match(e)})},stripIgnoredUrlParameters=function(e,t){var a=new URL(e);return a.hash="",a.search=a.search.slice(1).split("&").map(function(e){return e.split("=")}).filter(function(a){return t.every(function(e){return!e.test(a[0])})}).map(function(e){return e.join("=")}).join("&"),a.toString()},hashParamName="_sw-precache",urlsToCacheKeys=new Map(precacheConfig.map(function(e){var a=e[0],t=e[1],r=new URL(a,self.location),c=createCacheKey(r,hashParamName,t,/\.\w{8}\./);return[r.toString(),c]}));function setOfCachedUrls(e){return e.keys().then(function(e){return e.map(function(e){return e.url})}).then(function(e){return new Set(e)})}self.addEventListener("install",function(e){e.waitUntil(caches.open(cacheName).then(function(r){return setOfCachedUrls(r).then(function(t){return Promise.all(Array.from(urlsToCacheKeys.values()).map(function(a){if(!t.has(a)){var e=new Request(a,{credentials:"same-origin"});return fetch(e).then(function(e){if(!e.ok)throw new Error("Request for "+a+" returned a response with status "+e.status);return cleanResponse(e).then(function(e){return r.put(a,e)})})}}))})}).then(function(){return self.skipWaiting()}))}),self.addEventListener("activate",function(e){var t=new Set(urlsToCacheKeys.values());e.waitUntil(caches.open(cacheName).then(function(a){return a.keys().then(function(e){return Promise.all(e.map(function(e){if(!t.has(e.url))return a.delete(e)}))})}).then(function(){return self.clients.claim()}))}),self.addEventListener("fetch",function(a){if("GET"===a.request.method){var e,t=stripIgnoredUrlParameters(a.request.url,ignoreUrlParametersMatching),r="index.html";(e=urlsToCacheKeys.has(t))||(t=addDirectoryIndex(t,r),e=urlsToCacheKeys.has(t));var c="/asaf-project/pres1android/index.html";!e&&"navigate"===a.request.mode&&isPathWhitelisted(["^(?!\\/__).*"],a.request.url)&&(t=new URL(c,self.location).toString(),e=urlsToCacheKeys.has(t)),e&&a.respondWith(caches.open(cacheName).then(function(e){return e.match(urlsToCacheKeys.get(t)).then(function(e){if(e)return e;throw Error("The cached response that was expected is missing.")})}).catch(function(e){return console.warn('Couldn\'t serve response for "%s" from cache: %O',a.request.url,e),fetch(a.request)}))}});
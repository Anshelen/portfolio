'use strict';

$(function () {
  const token = $("meta[name='_csrf']").attr("content");
  const header = $("meta[name='_csrf_header']").attr("content");
  $(document).ajaxSend(function(e, xhr) {
    xhr.setRequestHeader(header, token);
  });

  const logoutLink = $('#logout');
  if (logoutLink.length) {
    logoutLink.click(function () {
      $('#logout-form').submit();
      return false;
    });
  }
});

<!-- Yandex.Metrika counter -->
(function(m,e,t,r,i,k,a){m[i]=m[i]||function(){(m[i].a=m[i].a||[]).push(arguments)};
  m[i].l=1*new Date();k=e.createElement(t),a=e.getElementsByTagName(t)[0],k.async=1,k.src=r,a.parentNode.insertBefore(k,a)})
(window, document, "script", "https://mc.yandex.ru/metrika/tag.js", "ym");

ym(68230450, "init", {
  clickmap:true,
  trackLinks:true,
  accurateTrackBounce:true,
  webvisor:true
});

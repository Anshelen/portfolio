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

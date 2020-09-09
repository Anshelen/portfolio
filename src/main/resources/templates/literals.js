const messages = {};
/*[+
[# th:each="key : ${@jsMessageKeys}"]
  messages[ [[${key}]] ] = [[${#messages.msg(key)}]]
[/]
+]*/
const ajax_urls = {};
/*[+
[# th:each="entry : ${@ajaxUrlMap}"]
  ajax_urls[ [[${entry.key}]] ] = [[@{${entry.value}}]]
[/]
+]*/

/*[- */
messages["contacts.js.validation.name.required"] = "Укажите от кого письмо";
messages["contacts.js.validation.name.minlength"] = "Введите не менее 2-х символов в поле 'Имя'";
messages["contacts.js.validation.name.maxlength"] = "Слишком длинное имя";
messages["contacts.js.validation.subject.required"] = "Тема письма обязательна к заполнению";
messages["contacts.js.validation.subject.minlength"] = "Введите не менее 2-х символов в заголовок";
messages["contacts.js.validation.subject.maxlength"] = "Слишком длинный заголовок";
messages["contacts.js.validation.text.required"] = "Заполните тело письма";
messages["contacts.js.validation.text.maxlength"] = "Слишком длинное письмо";

ajax_urls["sendMail"] = "/email/send";
ajax_urls["resendRegistrationEmail"] = "/resendRegistrationEmail";
ajax_urls["getAllUsers"] = "/admin/users";
/* -]*/

// ...
// Cria o FormPanel
var loginForm = new Ext.form.FormPanel({
    labelWidth: 80,
    url: 'login', // <<< MUDANÇA AQUI: Aponta para a URL do nosso novo servlet
    frame: true,
    title: 'Por favor, identifique-se',
    // ... o resto do código do formulário permanece exatamente o mesmo
});
// ...
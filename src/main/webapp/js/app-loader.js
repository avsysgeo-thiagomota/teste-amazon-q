Ext.onReady(function() {
    // Carrega os arquivos da aplicação na ordem correta de dependência
    Ext.Loader.load([
        'js/app/model/Receita.js',
        'js/app/store/Receitas.js',
        'js/app/view/ReceitaWindow.js',
        'js/app/view/ReceitaGrid.js'
    ], function() {
        // Namespace principal da aplicação
        Ext.ns('App');

        // Inicializa o sistema de dicas (tooltips)
        Ext.QuickTips.init();

        // Imagem transparente padrão do ExtJS
        Ext.BLANK_IMAGE_URL = 'extjs/resources/images/default/s.gif';

        // Cria a instância principal da nossa View (a Grid)
        var grid = new App.view.ReceitaGrid({
            renderTo: 'grid-receitas-container'
        });
    }, this, true); // O 'true' no final preserva a ordem de carregamento
});
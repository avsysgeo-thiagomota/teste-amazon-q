Ext.ns('App.model');

App.model.Ingrediente = Ext.data.Record.create([
    {name: 'nome', type: 'string'},
    {name: 'quantidade', type: 'float'},
    {name: 'unidade', type: 'string'}
]);

App.model.Passo = Ext.data.Record.create([
    {name: 'ordem', type: 'int'},
    {name: 'descricao', type: 'string'}
]);

App.model.Receita = Ext.data.Record.create([
    {name: 'id', type: 'int'},
    'nome',
    'descricao',
    {name: 'tempoDePreparo', type: 'int'},
    {name: 'porcoes', type: 'int'},
    'dificuldade',
    'categorias',
    {name: 'ingredientes', mapping: 'ingredientes'},
    {name: 'passos', mapping: 'passos'}
]);
# integrationtests

Denne modulen har ingen egen kode, bortsett fra testklassen AjourAppIT. Ved hjelp av oppsett med Maven og Failsafe-tillegget,
vil modulen ved testing starte opp serverapplikasjonen fra restserver-modulen med en testkonfigurasjon. Det samme gjøres
med JavaFx-applikasjonen fra fxgui-modulen, som har en egen konfigurasjon under resources som matcher serveren.



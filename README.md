# SimpleJavaFlightSimulator

##Milstolpar
###Done
- 1	Skapa nya/använd javas klasser för vektor och matris matematik

- 2	Implementera en ”Game” klass som sätter upp ett fönster med OpenGL-utritningsfunktionalitet. Skapa även en abstrakt klass för utritningsbart 3D-objekt som innerhåller funktioner för att rita ut sig själv.

- 3	Skapa ett testfordon som utökar ”utritningsbart 3D-objekt” detta fordon bör vara så simpelt som möjligt för att testa att utritningen fungerar.

- 4	Lägg till funktinalitet för att rita ut 3D-scenen utifrån ett perspektiv. Gör om testfördonet så att det har en mer avancerad form för bekräfta att det fungerar som det ska.

- 5	Skapa en terräng som utökar ”utritningsbart 3D-objekt”, denna bör genereras från en heightmap-BMP-bild.

- 6	Lägg till funktionalitet för att kunna styra kameran.

- 7	Lägg till funktionalitet för att kunna styra testfordonet med tangentbordet.

- 8	Fäst kameran vid fordonet

- 9	Implementera flygplansfysik samt lägg till så att kollision med mark ger GameOver(behöver inte vara exakt).

###To do

- 10	Lägg till möjlighet att respawna.

- 11	Lägg till någon form av skjutvapen som kan tilldelas till fordon. Skapa möjligheten att med hjälp av dessa kunna skjuta projektiler.

- 12	Skapa måltavlor som spawnar random på banan. Självklart ärver även dessa från ”utritningsbart 3D-objekt”.  Lägg till funktionalitet för att kontrollera om dessa träffats av eventuell avfyrad projektil.

- 13	Implementera poängsystem likt i tetrisprojektet

- 14	(Vid behov)Förfina markkollisionshanteringen. Lägg till möjlighet att kunna starta och landa planet på marken(utan GameOver).

- 15	Lägga till flera flygplan/helikoptrar.

- 16	Skapa någon form av menysystem eller launcher där man kan välja fordon, karta, ställa in grafikinställningar och liknande.

- 17	Gör så att terrängen renderas med textur.

- 18	Lägg till fler kartor. Snygga till grafiken. Exempelvis lite grundläggande ljussätning(mkt grundläggande).

- 19	Se till att exceptions under initialisering kommer upp som fina felmeddelanden som tydligt beskriver felet.

- 20	De exceptions som man bör kunna återhämta sig från bör hanteras på lämpligt sätt. De som inte kan hanteras automatiskt bör skicka användaren dit det går att åtgärda. Exempelvis till menyn för val av OpenGL- version eller liknande.

- 21	Lägg till begränsad ammunitionskapacitet. Ladda om på landningsbanan.

- 22	Lägg till någon form av skybox/dome för att ge en lite trevligare känsla.

- 23	Vid behov, förbättra flyg- samt kollisionsfysiken tills de upplevs tillfredsställande.

- 24	Lägg till stöd för större kartor. Skapa ett chunkloadingssystem där bara de synliga delarna av kartan är inladdade, för att spara minne.

- 25	Implementera funktionalitet att på lämpligt sätt kunna läsa av/sätta fordons position och riktning.

- 26	Implementera stöd för multiplayer med hjälp av M25.

- 27	Lägg till spelläge där man kan tävla mot andra spelare live i ett ”free for all” liknande game mode.

- 27	Ta bort eventuellt hackande orsakat av nätverket

- 28	Skapa ytterligare fordon och kartor. Lägga till roder och liknande som rör på sig då man ger utslag.

package de.timmyrs.varo;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

enum Message
{
	JOIN_SPECTATE("You can no longer partake in this Varo round.", "Du kannst in dieser Varo Runde leider nicht mehr mitmachen.", "Je kan niet langer deelnemen in deze ronde van Varo"),
	JOIN_CONTINUE("Welcome back. §cThe Varo round is still ongoing!", "Willkommen zurück. §cDie Varo Runde ist immernoch am laufen!", "Welkom terug. De Varo ronde is nog steeds gaande."),
	SPECTATE("Just spectate in the meantime. Teleportation: §6/varo tp <player>", "Schau solange doch einfach zu. Teleportation: §6/varo tp <Spieler>", "Bekijk het gevecht in de tussentijd. Teleportatie: §6/varo tp <speler>"),
	NEW_GAME_SOON("A new Varo round will start soon.", "Eine neue Varo Runde wird bald starten.", "Een nieuwe ronde van Varo zal zometeen plaatsvinden."),
	TEAM_INFO_1("In the meantime, use §6/team <player>§r to build a team.", "Bis dahin kannst du mit §6/team <Spieler>§r ein Team bauen.", "In de tussentijd, gebruik §6/team <speler>§r om een team te maken."),
	TEAM_INFO_2("If you're not in a team at start, you will be assigned one.", "Wenn du beim Start in keinem Team bist, wird dir eins zugewiesen.", "Als je voor de ronde nog niet in een team zit wordt je automatisch in een team in gezet."),
	DEATH("§cYou died. You still have % live(s)!", "§cDu bist gestorben. Du hast noch % Leben!", "§cJe bent gestorven. Je hebt nog % leven(s)!"),
	DEATH_FINAL("§cYou died and will only be able to partake again in the next game.", "§cDu bist gestorben§r und kannst wieder in der nächsten Runde mitspielen.", "§cJe bent gestorven en zal niet mee mogen doe in de volgende ronde."),
	WIN_SINGULAR("§a% has won!", "§a% hat gewonnen!", "§a% heeft gewonnen!"),
	WIN_MULTIPLE("§a% have won!", "§a% haben gewonnen!", "§a% hebben gewonnen"),
	LIST_SEPARATOR(", ", ", ", ", "),
	LIST_SEPARATOR_FINAL(", and ", " und ", "en"),
	TEAM_DISBAND("§cYour team has been disbanded.", "§cDein Team hat sich aufgelöst.", "§cJe team is ontbonden."),
	ERROR_UNAUTHORIZED("§cYou are not authorized to use this command.", "§cDu bist nicht authorisiert, diesen Befehl auszuführen.", "§cJe hebt geen toestemming om dit commando uit te voeren."),
	ERROR_ONGOING("§cThe Varo round has already started.", "§cDie Varo Runde ist bereits am laufen.", "§cDe Varo ronde is al begonnen."),
	ERROR_NOT_ONGOING("§cNo Varo round is ongoing.", "§cEs ist keine Varo Runde am laufen.", "§cEr is geen Varo ronde gaande."),
	ERROR_NO_TEAM("§cYou're not in a team.", "§cDu bist in keinem Team.", "§cJe zit nog niet in een team."),
	ERROR_NO_TEAMS("§cThere will be no teams in this Varo round.", "§cIn dieser Varo Runde wird es keine Teams geben.", "§cEr zullen geen teams zijn in deze Varo ronde."),
	ERROR_OFFLINE("§c% is not online.", "§c% is not online.", "§c% is niet online."),
	ERROR_TEAM_FULL("§cUnfortunately, %'s team is already full.", "§cDas Team von % ist leider schon voll.", "§cHelaas, het team van % is al vol."),
	ERROR_SELFTEAM("§cYou will always be in a team with yourself.", "§cDu wirst immer mit dir selbst in einem Team sein.", "§cJe zult altijd in een team met jezelf zitten."),
	ERROR_PLAYERS_ONLY("§cThis command is only for players.", "§cDieser Befehl ist nur für Spieler", "§cDit commando is bestemd voor spelers."),
	TEAM_JOINED("§aYou're now in a team with %.", "§aDu bist nun in einem Team mit %.", "§aJe zit nu in een team met %."),
	TEAM_JOIN("§a% is now in your team.", "§a% ist nun in deinem Team.", "§a% zit nu in je team."),
	SYNTAX_TEAM("§cSyntax: /team [[info]|[invite ]<player>|help|requests|leave]", "§cSyntax: /team [[info]|[invite ]<Spieler>|help|requests|leave]","§cUitvoering: /team [[info]|[invite ]<speler>|help|requests|leave]"),
	TEAMREQ_SENT_1("§aYou've sent a team request to %.", "§aDu hast eine Team-Anfrage an % gesendet.", "§aJe hebt een team-aanvraag naar % getuurd."),
	TEAMREQ_SENT_2("They must now run §6/team %§r to join.", "Diese(r) muss nun §6/team %§r ausführen, um beizutreten.", "Deze speler moet nu §6/team %§r uitvoeren."),
	TEAMREQ_OUT_NONE("Outgoing Team Requests: None", "Ausgehende Team-Anfragen: Keine", "Uitgaande team uitnodigingen: Geen"),
	TEAMREQ_OUT("Outgoing Team Requests to:", "Ausgehende Team-Anfragen an:", "Uitgaande team uitnodigingen naar:"),
	TEAMREQ_IN_NONE("Incoming Team Requests: None", "Eingehende Team-Anfragen: Keine", "Inkomende team uitnodigingen van: Niemand"),
	TEAMREQ_IN("Incoming Team Requests from:", "Eingehende Team-Anfragen von:", "Inkomende team uitnodigingen van:"),
	SYNTAX_VARO("§cSyntax: /varo [tp <player>|start|end|savedefaultitems|flush|reload]", "§cSyntax: /varo [tp <Spieler>|start|end|savedefaultitems|flush|reload]", "§cUitvoering: /varo [tp <speler>|start|end|savedefaultitems|flush|reload]"),
	TELEPORT_UNAUTHORIZED("§cOnly spectators and admins are allowed to teleport.", "§cNur Zuschauer und Admins dürfen sich teleportieren.", "§cAlleen de doden en Admins mogen teleporteren."),
	SAVED_DEFAULT_ITEMS("§aYour inventory has been saved as the start inventory for new Varo rounds.", "§aDein Inventar wurde als das Start-Inventar für neue Varo Runden gespeichert.", "§aJe inventory is opgleslagen als je start inventory voor de volgende ronde."),
	FLUSH_OK("§aFlushed configuration to disk.", "§aDie Konfiguration wurde auf die Platte geschrieben.",  "§aFlushed-configuratie naar schijf."),
	RELOAD_OK("§aReloaded configuration from disk.", "§aDie Konfiguration wurde von der Platte geladen.", "§aConfiguratie van schijf herladen."),
	SYNTAX_TEAMMESSAGE("§cSyntax: /t <message>", "§cSyntax: /t <Nachricht>", "§cUitvoering: /t <bericht>"),
	TEAM_LEFT("§aYou are no longer in a team.", "§aDu bist nun in keinem Team mehr.", "§aJe zit niet langer in een team."),
	START_INSUFFICIENT_PLAYERS("§cThere are not enough players to start.", "§cEs sind nicht genug Spieler zum starten da.", "§cEr zijn niet genoeg spelers om te starten."),
	GET_READY("§eGet ready!", "§eMach dich bereit!", "§eMaak je klaar!"),
	HAVE_FUN("§aHave fun!", "§aViel Spaß!", "§aVeel plezier!"),
	PREMATURE_END("The Varo round has been terminated prematurely.", "Die Varo Runde wurde frühzeitig beendet.", "Deze Varo ronde is vroegtijdig beëindigd."),
	PREMATURE_END_BY("The Varo round has been terminated prematurely by %.", "Die Varo Runde wurde frühzeitig von % beendet.", "Deze Varo ronde is vroegtijdig beëindigd door %.");

	final String en;
	final String de;
	final String nl;

	Message(String en, String de, String nl)
	{
		this.en = en;
		this.de = de;
		this.nl = nl;
	}

	String get(Player recipient)
	{
		final String lang = recipient.getLocale().substring(0, 2).toLowerCase();
		if(lang.equals("de"))
		{
			return de;
		}
		if(lang.equals("nl"))
		{
			return nl;
		}
		return en;
	}

	void send(Player recipient)
	{
		recipient.sendMessage(this.get(recipient));
	}

	void send(CommandSender recipient)
	{
		recipient.sendMessage(recipient instanceof Player ? this.get((Player) recipient) : this.en);
	}
}

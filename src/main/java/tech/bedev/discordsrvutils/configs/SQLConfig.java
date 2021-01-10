package tech.bedev.discordsrvutils.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import static space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;

public interface SQLConfig
{
	@DefaultBoolean(false)
	@ConfComments("#Should we use a database?")
	@AnnotationBasedSorter.Order(1)
	boolean isEnabled();

	@DefaultString("localhost")
	@ConfComments("\n#Host for your database, usually localhost.")
	@AnnotationBasedSorter.Order(2)
	String getHost();

	@DefaultInteger(3306)
	@ConfComments("\n#Port for your Database, usually 3306")
	@AnnotationBasedSorter.Order(3)
	int getPort();

	@DefaultString("root")
	@ConfComments("\n#Username used to login to database.")
	@Order(4)
	String getUsername();

	@DefaultString("password")
	@ConfComments("\n#Password used to login to database.")
	@Order(5)
	String getPassword();

	@DefaultString("DiscordSRVUtilsData")
	@ConfComments("\n#Database name. The host should tell you the name normally.")
	@AnnotationBasedSorter.Order(6)
	String getDatabaseName();
}

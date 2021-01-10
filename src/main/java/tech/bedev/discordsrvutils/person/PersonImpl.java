package tech.bedev.discordsrvutils.person;


import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import org.bukkit.Bukkit;
import tech.bedev.discordsrvutils.DiscordSRVUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PersonImpl implements Person
{

	private final UUID uuid;
	private final Member discordUser;
	private DiscordSRVUtils core;
	private int level = -1;
	private int xp = -1;
	private Long minecraftMessages = -1L;
	private Long discordMessages = -1L;

	public PersonImpl(UUID uuid, Member DiscordUser, DiscordSRVUtils core)
	{
		this.discordUser = DiscordUser;
		this.core = core;
		this.uuid = uuid;
		insertLeveling();
		reloadCache();

	}

	@Override
	public void addLevels(int levels)
	{
		insertLeveling();
		try(Connection conn = core.getDatabaseFile())
		{
			if(uuid != null)
			{
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
				p1.setString(1, uuid.toString());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=? WHERE unique_id=?");
					p2.setInt(1, r1.getInt("level") + levels);
					p2.setString(2, uuid.toString());
					p2.execute();
					this.level = r1.getInt("level") + levels;
				}
			}
			else
			{
				if(discordUser == null) return;
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
				p1.setLong(1, discordUser.getIdLong());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=? WHERE userID=?");
					p2.setInt(1, r1.getInt("level") + levels);
					p2.setLong(2, discordUser.getIdLong());
					p2.execute();
					this.level = r1.getInt("level") + levels;
				}

			}

		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void removeLevels(int levels)
	{
		insertLeveling();
		try(Connection conn = core.getDatabaseFile())
		{
			if(uuid != null)
			{
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
				p1.setString(1, uuid.toString());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=? WHERE unique_id=?");
					p2.setInt(1, r1.getInt("level") - levels);
					p2.setString(2, uuid.toString());
					p2.execute();
					this.level = r1.getInt("level") - levels;
				}
			}
			else
			{
				if(discordUser == null) return;
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
				p1.setLong(1, discordUser.getIdLong());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=? WHERE userID=?");
					p2.setInt(1, r1.getInt("level") - levels);
					p2.setLong(2, discordUser.getIdLong());
					p2.execute();
					this.level = r1.getInt("level") - levels;
				}

			}

		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}


	}

	@Override
	public void addXP(int xp)
	{
		insertLeveling();
		try(Connection conn = core.getDatabaseFile())
		{
			if(uuid != null)
			{
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
				p1.setString(1, uuid.toString());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=? WHERE unique_id=?");
					p2.setInt(1, r1.getInt("XP") + xp);
					p2.setString(2, uuid.toString());
					p2.execute();
					this.xp = r1.getInt("XP") + xp;
				}
			}
			else
			{
				if(discordUser == null) return;
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
				p1.setLong(1, discordUser.getIdLong());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=? WHERE userID=?");
					p2.setInt(1, r1.getInt("XP") + xp);
					p2.setLong(2, discordUser.getIdLong());
					p2.execute();
					this.xp = r1.getInt("XP") + xp;
				}

			}

		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}

	}

	@Override
	public void removeXP(int xp)
	{
		insertLeveling();
		try(Connection conn = core.getDatabaseFile())
		{
			if(uuid != null)
			{
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
				p1.setString(1, uuid.toString());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=? WHERE unique_id=?");
					p2.setInt(1, r1.getInt("XP") - xp);
					p2.setString(2, uuid.toString());
					p2.execute();
					this.xp = r1.getInt("XP") - xp;
				}
			}
			else
			{
				if(discordUser == null) return;
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
				p1.setLong(1, discordUser.getIdLong());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=? WHERE userID=?");
					p2.setInt(1, r1.getInt("XP") - xp);
					p2.setLong(2, discordUser.getIdLong());
					p2.execute();
					this.xp = r1.getInt("XP") - xp;
				}

			}

		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}

	}

	@Override
	public void clearXP()
	{
		insertLeveling();
		try(Connection conn = core.getDatabaseFile())
		{
			if(uuid != null)
			{
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
				p1.setString(1, uuid.toString());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=0 WHERE unique_id=?");
					p2.setString(1, uuid.toString());
					p2.execute();
					this.xp = 0;
				}
			}
			else
			{
				if(discordUser == null) return;
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
				p1.setLong(1, discordUser.getIdLong());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=0 WHERE userID=?");
					p2.setLong(1, discordUser.getIdLong());
					p2.execute();
					this.xp = 0;
				}
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}


	}

	@Override
	public int getLevel()
	{
		return level;
	}

	@Override
	public void setLevel(int level)
	{
		insertLeveling();
		try(Connection conn = core.getDatabaseFile())
		{
			if(uuid != null)
			{
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
				p1.setString(1, uuid.toString());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=? WHERE unique_id=?");
					p2.setInt(1, level);
					p2.setString(2, uuid.toString());
					p2.execute();
					this.level = level;
				}
			}
			else
			{
				if(discordUser == null) return;
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
				p1.setLong(1, discordUser.getIdLong());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=? WHERE userID=?");
					p2.setInt(1, level);
					p2.setLong(2, discordUser.getIdLong());
					p2.execute();
					this.level = level;
				}

			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}

	}

	@Override
	public int getXP()
	{
		return xp;
	}

	@Override
	public void setXP(int xp)
	{
		insertLeveling();
		try(Connection conn = core.getDatabaseFile())
		{
			if(uuid != null)
			{
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
				p1.setString(1, uuid.toString());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=? WHERE unique_id=?");
					p2.setInt(1, xp);
					p2.setString(2, uuid.toString());
					p2.execute();
					this.xp = xp;
				}
			}
			else
			{
				if(discordUser == null) return;
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
				p1.setLong(1, discordUser.getIdLong());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(r1.next())
				{
					PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=? WHERE userID=?");
					p2.setInt(2, xp);
					p2.setLong(2, discordUser.getIdLong());
					p2.execute();
					this.xp = xp;
				}

			}

		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}


	}

	@Override
	public String getRank()
	{
		insertLeveling();
		if(uuid != null)
		{
			try(Connection conn = core.getDatabaseFile())
			{
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling ORDER BY Level DESC");
				ResultSet r1 = p1.executeQuery();
				int rank = 0;
				while(r1.next())
				{
					rank++;
					if(r1.getString("unique_id").equals(uuid.toString()))
					{
						return Integer.toString(rank);
					}
				}
			}
			catch(SQLException ex)
			{
				ex.printStackTrace();
			}
		}
		return "Unknown";
	}

	@Override
	public boolean isLinked()
	{
		int count = 0;
		if(uuid != null) count++;
		if(discordUser != null) count++;
		return count == 2;
	}

	@Override
	public UUID getMinecraftUUID()
	{
		return this.uuid;
	}

	@Override
	public void insertLeveling()
	{
		try(Connection conn = core.getDatabaseFile())
		{
			if(uuid != null)
			{
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
				p1.setString(1, uuid.toString());
				p1.execute();
				ResultSet r1 = p1.executeQuery();
				if(!r1.next())
				{
					if(discordUser == null)
					{
						PreparedStatement p2 = conn.prepareStatement("INSERT INTO discordsrvutils_leveling (unique_id, XP, level) VALUES (?, 0, 0)");
						p2.setString(1, uuid.toString());
						p2.execute();
					}
					else
					{
						PreparedStatement p2 = conn.prepareStatement("INSERT INTO discordsrvutils_leveling (unique_id, XP, level, userID) VALUES (?, 0, 0, ?)");
						p2.setString(1, uuid.toString());
						p2.setLong(2, discordUser.getIdLong());
						p2.execute();

					}
				}
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public Long getDiscordMessages()
	{
		return discordMessages;
	}

	@Override
	public Long getMinecraftMessages()
	{
		return minecraftMessages;
	}

	@Override
	public Long getTotalMessages()
	{
		return discordMessages + minecraftMessages;
	}

	@Override
	public void addMessages(MessageType msg, int number)
	{
		if(uuid == null) return;
		insertLeveling();
		try(Connection conn = core.getDatabaseFile())
		{
			switch(msg)
			{
				case DISCORD:
					PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
					p1.setString(1, uuid.toString());
					ResultSet r1 = p1.executeQuery();
					if(r1.next())
					{
						PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET DiscordMessages=? WHERE unique_id=?");
						p2.setLong(1, r1.getLong("DiscordMessages") + number);
						p2.setString(2, uuid.toString());
						p2.execute();
						discordMessages = r1.getLong("DiscordMessages") + number;
					}
					break;
				case MINECRAFT:
					PreparedStatement p2 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
					p2.setString(1, uuid.toString());
					ResultSet r2 = p2.executeQuery();
					if(r2.next())
					{
						PreparedStatement p3 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET MinecraftMessages=? WHERE unique_id=?");
						p3.setLong(1, r2.getLong("MinecraftMessages") + number);
						p3.setString(2, uuid.toString());
						p3.execute();
						minecraftMessages = r2.getLong("MinecraftMessages") + number;
					}
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}

	}

	@Override
	public boolean isBukkitCached()
	{
		if(uuid == null) return false;
		if(Bukkit.getOfflinePlayer(uuid).getName() == null) return false;
		return true;
	}

	@Override
	public void reloadCache()
	{
		try(Connection conn = core.getDatabaseFile())
		{
			PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
			p1.setString(1, uuid.toString());
			ResultSet r1 = p1.executeQuery();
			if(r1.next())
			{
				level = r1.getInt("Level");
				xp = r1.getInt("XP");
				minecraftMessages = r1.getLong("MinecraftMessages");
				discordMessages = r1.getLong("DiscordMessages");
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}
	}

}

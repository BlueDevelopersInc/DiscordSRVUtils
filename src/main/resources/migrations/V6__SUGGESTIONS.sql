CREATE TABLE suggestions(SuggestionNumber int PRIMARY KEY, SuggestionText LONGTEXT, Submitter BIGINT, MessageID BIGINT, ChannelID BIGINT, CreationTime BIGINT, Approved varchar(5));

CREATE TABLE suggestion_notes(StaffID BIGINT,NoteText LONGTEXT,SuggestionNumber int, CreationTime BIGINT)
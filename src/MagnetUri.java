public class MagnetUri
{
	public String m_URI;
	public String m_displayName;
	public String m_hash;
	public String m_filename;

	public MagnetUri(String _Uri, String _TargetDirectory)
	{
		m_URI = _Uri;

		m_displayName = "";
		int tagPos = 0;
		int ampPos = 0;

		tagPos = m_URI.indexOf("urn:");
		tagPos += 4;
		ampPos = m_URI.indexOf("&", tagPos);
		tagPos = m_URI.lastIndexOf(":", ampPos) + 1;
		m_hash = m_URI.substring(tagPos, ampPos);

		tagPos = m_URI.indexOf("dn=");
		if (tagPos > -1)
		{
			ampPos = m_URI.indexOf("&", tagPos);
			if (ampPos > tagPos)
				m_displayName = m_URI.substring(tagPos + 3, ampPos);
		}

		if (m_displayName.isEmpty())
			m_filename = "meta-" + m_hash + ".torrent";
		else
			m_filename = "meta-" + m_displayName + ".torrent";

		m_filename = _TargetDirectory + "\\" + m_filename;
	}

	public String getAsTorrentData()
	{
		String Data = "d10:magnet-uri" + m_URI.length() + ":" + m_URI + "e";
		return Data;
	}
}

class Target
{
	public String m_Name;
	public String m_Target;
};

public class MagnetSettings
{
    static String getKeyName(int i)
    {
    	return "Software\\MagnetPopup\\Scripts\\Script"+i;
    }
	
	public static void SetTarget(int _index, Target _target)
	{
    	String key = getKeyName(_index);
    	try
    	{
    		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, key);
    		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, key, "Name", _target.m_Name);
    		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, key, "Target", _target.m_Target);
    	} catch (Exception e) {
    		System.out.println(e);
    	}
	}
	
    public static Target GetTarget(int _index)
    {
    	Target target = new Target();
    	String key = getKeyName(_index);
    	try
    	{
    		target.m_Name = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, key, "Name");
    		target.m_Target = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, key, "Target");
    	} catch (Exception e) {
    	}
		return target;
    }
    
    public static void ClearTarget(int i)
    {
    	String key = getKeyName(i);
    	try
    	{
    		WinRegistry.deleteKey(WinRegistry.HKEY_CURRENT_USER, key);
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    }

}

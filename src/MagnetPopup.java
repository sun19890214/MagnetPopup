import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.event.DocumentListener;

public class MagnetPopup implements ActionListener
{
	final JDialog dialog = new JDialog((Window) null, "MagnetPopup");

	public JFrame SettingsFrame;
	static String[] g_args;

	ArrayList<JButton> m_lButtons = new ArrayList<JButton>();

	public static void CenterFrameOnMouse(Window _window)
	{
		Point mousePos = MouseInfo.getPointerInfo().getLocation();
		mousePos.x -= _window.getWidth() * 0.5;
		mousePos.y -= _window.getHeight() * 0.5;
		_window.setLocation(mousePos);
	}

	public static void updateButtons(MagnetPopup _app)
	{
		Component contents = _app.createComponents();
		_app.dialog.getContentPane().removeAll();
		_app.dialog.getContentPane().add(contents, BorderLayout.CENTER);

	}

	class EditPopup implements ActionListener, DocumentListener
	{
		final JFrame frame = new JFrame("Script Settings");
		public int i;
		public MagnetPopup m_app;
		JTextField targetField;

		public void updateName(javax.swing.text.Document _document)
		{
			Target target = MagnetSettings.GetTarget(i);
			try
			{
				target.m_Name = _document.getText(0, _document.getLength());
			} catch (Exception e)
			{
			}
			MagnetSettings.SetTarget(i, target);
			m_app.UpdateButtons();
			frame.toFront();
		}

		public void changedUpdate(DocumentEvent e)
		{
			updateName(e.getDocument());
		}

		public void removeUpdate(DocumentEvent e)
		{
			updateName(e.getDocument());
		}

		public void insertUpdate(DocumentEvent e)
		{
			updateName(e.getDocument());
		}

		private void createAndShowGUI(MagnetPopup _app, int _i)
		{
			m_app = _app;
			i = _i;

			if (SettingsFrame != null)
				SettingsFrame.dispose();

			m_app.SettingsFrame = frame;

			JPanel pane = new JPanel(new GridLayout(3, 2, 5, 5));

			{
				JLabel nameLabel = new JLabel("Name");
				pane.add(nameLabel);
			}

			Target target = MagnetSettings.GetTarget(i);

			JTextField nameField = new JTextField(20);
			nameField.setText(target.m_Name);
			nameField.getDocument().addDocumentListener(this);
			pane.add(nameField);

			{
				JLabel nameLabel = new JLabel("Target Directory");
				pane.add(nameLabel);
			}

			targetField = new JTextField(target.m_Target);
			targetField.setEditable(false);
			pane.add(targetField);

			JButton scriptRemoverButton = new JButton("Remove target");
			scriptRemoverButton.setActionCommand("deleteTarget");
			scriptRemoverButton.addActionListener(this);
			pane.add(scriptRemoverButton);

			JButton scriptChooserButton = new JButton("Choose target");
			scriptChooserButton.setActionCommand("showFileChooser");
			scriptChooserButton.addActionListener(this);
			pane.add(scriptChooserButton);

			pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			frame.getContentPane().add(pane, BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
			Point pos = m_app.dialog.getLocation();
			pos.x += m_app.dialog.getWidth() + 5;
			pos.y += m_app.m_lButtons.get(i).getLocation().y;
			frame.setLocation(pos);
		}

		public void actionPerformed(ActionEvent e)
		{
			System.out.println(e);
			if (e.getActionCommand() == "showFileChooser")
			{
				JButton button = (JButton) e.getSource();
				Target target = MagnetSettings.GetTarget(i);
				final JFileChooser fc = new JFileChooser(target.m_Target);
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.showOpenDialog(button);
				target.m_Target = fc.getSelectedFile().getPath();
				MagnetSettings.SetTarget(i, target);
				m_app.UpdateButtons();
				targetField.setText(target.m_Target);
			} else if (e.getActionCommand() == "deleteTarget")
			{
				MagnetSettings.ClearTarget(i);
				m_app.UpdateButtons();
				frame.dispose();
			}
		}
	};

	class MouseInterface extends MouseAdapter
	{
		boolean pressed;
		MagnetPopup m_app;

		public MouseInterface(MagnetPopup _app)
		{
			m_app = _app;
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			pressed = true;
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			JButton clickedButton = (JButton) e.getComponent();
			if (clickedButton != null && pressed)
			{
				if (SwingUtilities.isRightMouseButton(e))
				{
					EditPopup editPopup = new EditPopup();
					int i = Integer.parseInt(clickedButton.getName());
					editPopup.createAndShowGUI(m_app, i);
				} else
				{
				}
			}
			pressed = false;
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			pressed = false;
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			pressed = true;
		}
	};

	MouseInterface m_MouseInteface = new MouseInterface(this);

	public void UpdateButtons()
	{
		JPanel pane = (JPanel) dialog.getContentPane().getComponent(0);
		dialog.remove(pane);
		recreateButtons(pane);
		dialog.getContentPane().add(pane, BorderLayout.CENTER);
		dialog.pack();
		dialog.setVisible(true);
	}

	public void recreateButtons(JPanel pane)
	{
		pane.removeAll();
		m_lButtons.clear();
		for (int i = 0; i < 255; ++i)
		{
			Target target = MagnetSettings.GetTarget(i);

			if (target.m_Name != null)
			{
				JButton button = new JButton(target.m_Name);
				button.addMouseListener(m_MouseInteface);
				button.setToolTipText(target.m_Target);
				if (i < 10)
					button.setMnemonic(KeyEvent.VK_1 + i);
				button.setName(String.valueOf(i));
				button.addActionListener(this);
				button.setActionCommand("createTarget");
				m_lButtons.add(button);
				pane.add(button);
			}
		}

		JPanel sep = new JPanel();
		pane.add(sep);

		JButton button = new JButton("Add target..");
		button.setMnemonic(KeyEvent.VK_A);
		button.setActionCommand("addTarget");
		button.addActionListener(this);

		pane.add(button);
		pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

	}

	public Component createComponents()
	{
		JPanel pane = new JPanel(new GridLayout(0, 1, 1, 1));

		recreateButtons(pane);

		return pane;
	}

	public void actionPerformed(ActionEvent e)
	{
		JButton button = (JButton) e.getSource();
		if (button != null)
		{
			String actionCommand = e.getActionCommand();
			if (actionCommand == "addTarget")
			{
				int i = m_lButtons.size();
				Target newTarget = new Target();
				newTarget.m_Name = "Script" + i;
				newTarget.m_Target = "c:\\";
				MagnetSettings.SetTarget(i, newTarget);
				UpdateButtons();
				if (SettingsFrame != null)
					SettingsFrame.dispose();
				return;
			} else if (actionCommand == "createTarget")
			{
				String targetDirectory = button.getToolTipText();
				if (targetDirectory != null)
				{
					for (String magnetString : g_args)
					{
						MagnetUri magnet = new MagnetUri(magnetString,
								targetDirectory);

						System.out.println("Torrent hash is: " + magnet.m_hash);
						System.out.println("Torrent name is: "
								+ magnet.m_displayName);
						System.out.println("Torrent destination is: "
								+ magnet.m_filename);

						System.out.println("Torrent data is: "
								+ magnet.getAsTorrentData());

						try
						{
							FileWriter fstream = new FileWriter(
									magnet.m_filename);
							BufferedWriter out = new BufferedWriter(fstream);
							out.write(magnet.getAsTorrentData());
							out.close();
						} catch (Exception e2)
						{
							System.err.println("Error: " + e2.getMessage());
						}

					}
					System.exit(0);
					return;
				}
			}
		}
		System.out.println("Unknown action" + e.getActionCommand());
	}

	private static void initLookAndFeel()
	{
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

		try
		{
			UIManager.setLookAndFeel(lookAndFeel);
			// MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
			// UIManager.setLookAndFeel(new MetalLookAndFeel());
		} catch (ClassNotFoundException e)
		{
			System.err
					.println("Couldn't find class for specified look and feel:"
							+ lookAndFeel);
			System.err
					.println("Did you include the L&F library in the class path?");
			System.err.println("Using the default look and feel.");
		} catch (UnsupportedLookAndFeelException e)
		{
			System.err.println("Can't use the specified look and feel ("
					+ lookAndFeel + ") on this platform.");
			System.err.println("Using the default look and feel.");
		}

		catch (Exception e)
		{
			System.err.println("Couldn't get specified look and feel ("
					+ lookAndFeel + "), for some reason.");
			System.err.println("Using the default look and feel.");
			e.printStackTrace();
		}
	}

	private static void createAndShowGUI()
	{
		// Set the look and feel.
		initLookAndFeel();

		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.

		MagnetPopup app = new MagnetPopup();

		if (g_args.length == 0)
		{
			JOptionPane.showMessageDialog(app.dialog, "No arguments");
			app.dialog.dispose();
			return;
		}

		app.dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		app.dialog.setIconImage(null);

		updateButtons(app);

		// Display the window.
		app.dialog.pack();
		app.dialog.setVisible(true);
		CenterFrameOnMouse(app.dialog);
		app.dialog.setResizable(false);
	}

	public static void main(String[] _args)
	{
		g_args = _args;

		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				createAndShowGUI();
			}
		});
	}
}
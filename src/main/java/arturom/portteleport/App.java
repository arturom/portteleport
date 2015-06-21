package arturom.portteleport;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Binds a local port to a port of a remote server accessed
 * through SSH.
 * 
 * Tunnel is created from CLI options
 *
 */
public class App {

	public static final String
        HELP            = "?",
        VERSION         = "v",
        SSH_USER        = "u",
        SSH_RSA_ID      = "i",
        SSH_HOST        = "h",
        SSH_PORT        = "p",
        REMOTE_HOST     = "r",
        REMOTE_PORT     = "m",
        LOCAL_PORT      = "l";

	public static void main(String[] args) {
		CommandLineParser parser = new DefaultParser();
		Options options = getInformationOptions();
		CommandLine cmd;
		
		// Parse information options only
		try {
			cmd = parser.parse(options, args);
			if (cmd.hasOption(HELP)) {
				options = getApplicationOptions(options);
				printCLIUsage(options);
				return;
			} else if (cmd.hasOption(VERSION)) {
				System.out.println("version: latest");
				return;
			}
		} catch(UnrecognizedOptionException e) {
		} catch(ParseException e) {
			handleException(e, options);
		}
		
		// Parse the application options
		try{
			options = getApplicationOptions(options);
			cmd = parser.parse(options, args);
			Session session = getSession(cmd);
			session.connect();
			System.out.println("Connected");
		} catch(Exception e) {
			handleException(e, options);
		}
	}
	
	/**
	 * Prints the exception message along with the available application options
	 * 
	 * @param e
	 * @param options
	 */
	private static void handleException(Exception e, Options options) {
			options = getApplicationOptions(options);

			System.out.println(e.getMessage());
			System.out.println();
			e.printStackTrace();
			System.out.println();
			System.out.println();
			printCLIUsage(options);

			System.exit(1);
	}
	
	/**
	 * Prints the available application options
	 * 
	 * @param options
	 */
	private static void printCLIUsage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("portteleport", options);
	}
	
	private static Options getInformationOptions() {
		Options options = new Options()
			.addOption(HELP, "help", false, "Print this menu")
			.addOption(VERSION, "version", false, "Display the version number");
		return options;
	}

	private static Options getApplicationOptions(Options options) {
		 options
            .addOption(
                Option.builder(SSH_USER)
                .longOpt("user")
                .argName("username")
                .desc("Username to log in as in remote server")
                .required()
                .hasArg()
                .build()
            )
            .addOption(
                Option.builder(SSH_RSA_ID)
                .longOpt("rsaid")
                .argName("filepath")
                .desc("Path to RSA ID file")
                .required()
                .hasArg()
                .build()
            )
            .addOption(
                Option.builder(SSH_HOST)
                .longOpt("sshhost")
                .argName("hostname")
                .desc("Hostname or IP of the SSH server")
                .required()
                .hasArg()
                .build()
            )
            .addOption(
                Option.builder(SSH_PORT)
                .longOpt("sshport")
                .argName("port")
                .desc("SSH port number")
                .type(int.class)
                .required()
                .hasArg()
                .build()
            )
            .addOption(
                Option.builder(REMOTE_HOST)
                .longOpt("remotehost")
                .argName("hostname")
                .desc("Remote host name or IP")
                .required()
                .hasArg()
                .build()
            )
            .addOption(
                Option.builder(REMOTE_PORT)
                .longOpt("remoteport")
                .argName("port")
                .desc("Port name of the remote host")
                .type(int.class)
                .required()
                .hasArg()
                .build()
            )
            .addOption(
                Option.builder(LOCAL_PORT)
                .longOpt("localport")
                .argName("port")
                .desc("The port to open on the local machine")
                .type(int.class)
                .required()
                .hasArg()
                .build()
            );
		return options;
	}
	
	private static Session getSession(CommandLine cmd) throws JSchException {
        final JSch jsch = new JSch();

        jsch.addIdentity(cmd.getOptionValue(SSH_RSA_ID));;

        Session session = jsch.getSession(
        		cmd.getOptionValue(SSH_USER),
        		cmd.getOptionValue(SSH_HOST),
        		Integer.parseInt(cmd.getOptionValue(SSH_PORT))
        );
        
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPortForwardingL(
        		Integer.parseInt(cmd.getOptionValue(LOCAL_PORT)),
        		cmd.getOptionValue(REMOTE_HOST),
        		Integer.parseInt(cmd.getOptionValue(REMOTE_PORT))
        );
        return session;
	}
}
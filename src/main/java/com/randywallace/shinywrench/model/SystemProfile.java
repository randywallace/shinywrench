package com.randywallace.shinywrench.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemProfile {

	private static Logger LOG = LoggerFactory.getLogger(SystemProfile.class);
	private String credential_file_path;
	private String config_file_path;
	private ObservableList<Profile> profileData = FXCollections.observableArrayList(Profile.extractor());
	private Ini credential_ini;
	private Ini config_ini;

	private static SystemProfile instance;

	private SystemProfile() {
		this.credential_ini = new Ini();
		this.config_ini = new Ini();
		this.credential_file_path = System.getProperty("user.home") + 
				File.separator + ".aws" + File.separator + "credentials";
		this.config_file_path = System.getProperty("user.home") + 
				File.separator + ".aws" + File.separator + "config";
		Path aws_config_dir = Paths.get(System.getProperty("user.home") + "/.aws");
		try {
			// TODO Add directory attributes
			Files.createDirectory(aws_config_dir);
		} catch (FileAlreadyExistsException e) {
			LOG.info(aws_config_dir.toString() + " already exists");
		} catch (Exception e) {
		    LOG.error(e.getMessage(), e);
		    System.exit(2);
		}
		try {
			// TODO Add file attributes
			Files.createFile(Paths.get(this.config_file_path));
		} catch (FileAlreadyExistsException e) {
			LOG.info(this.config_file_path + " already exists");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		    System.exit(2);
		}
		try {
			// TODO Add file attributes
			Files.createFile(Paths.get(this.credential_file_path));
		} catch (FileAlreadyExistsException e) {
			LOG.info(this.credential_file_path + " already exists");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		    System.exit(2);
		}

		try {
			this.credential_ini.load(new FileReader(this.credential_file_path));
			this.config_ini.load(new FileReader(this.config_file_path));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage(), e);
			System.exit(2);
		}
		LOG.debug(credential_ini.get("default").toString());
		for (Entry<String, Section> entry : this.config_ini.entrySet()) {
			String local_region = entry.getValue().get("region");
			if (local_region == null || local_region.isEmpty()) {
				local_region = "us-east-1";
			}
			String local_profile = entry.getKey();
			String[] local_profile_split = local_profile.split(" ");
			if (local_profile_split.length == 2) {
				local_profile = local_profile_split[1];
			}

			this.profileData.add(
					new Profile(
							local_profile,
							entry.getValue().get("aws_access_key_id"),
							entry.getValue().get("aws_secret_access_key"),
							local_region,
							entry.getValue().get("output"),
							entry.getValue().get("aws_session_token"),
							entry.getValue().get("role_arn"),
							entry.getValue().get("source_profile"),
							entry.getValue().get("mfa_serial"),
							entry.getValue().get("expiration")));
		}

		for (Entry<String, Section> entry : this.credential_ini.entrySet()) {
			Boolean updated = false;
			for (Profile profile : this.profileData) {
				LOG.debug(profile.getProfile() + " " + entry.getKey());
				if (profile.getProfile().equals(entry.getKey())) {
					profile.setAccessKeyId(entry.getValue().get("aws_access_key_id"));
					profile.setSecretAccessKey(entry.getValue().get("aws_secret_access_key"));
					profile.setSessionToken(entry.getValue().get("aws_session_token"));
					updated = true;
				}
			}
			if (!updated) {
				this.profileData.add(
						new Profile(
								entry.getKey(),
								entry.getValue().get("aws_access_key_id"),
								entry.getValue().get("aws_secret_access_key"),
								"us-east-1",
								null,
								entry.getValue().get("aws_session_token"),
								null,
								null,
								null,
								null));
			}
		}

		LOG.debug(this.profileData.toString());
	}

	public static SystemProfile getInstance(){
		if(instance == null){
			instance = new SystemProfile();
		}
		return instance;
	}

	public ObservableList<Profile> getProfileData() {
		return this.profileData;
	}

	public String getCredential_file_path() {
		return this.credential_file_path;
	}

	public void setCredential_file_path(String credential_file_path) {
		this.credential_file_path = credential_file_path;
	}

	public String getConfig_file_path() {
		return this.config_file_path;
	}

	public void setConfig_file_path(String config_file_path) {
		this.config_file_path = config_file_path;
	}

	public Profile getProfileByName(String profile_name) {
		for (Profile profile: this.profileData ) {
			if ( profile.getProfile().equals(profile_name)) {
				return profile;
			}
		}
		throw new RuntimeException("Profile " + profile_name + " does not exist!");
	}

	public void saveConfig() {
		this.credential_ini = new Ini();
		this.config_ini = new Ini();

		for (Profile profile : this.profileData) {
			String profile_name = profile.getProfile();
			this.credential_ini.add(profile_name, "aws_access_key_id", profile.getAccessKeyId());
			this.credential_ini.add(profile_name, "aws_secret_access_key", profile.getSecretAccessKey());
			this.credential_ini.add(profile_name, "aws_session_token", profile.getSessionToken());
			try {
				this.credential_ini.store(new File(this.credential_file_path));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOG.error(e.getMessage(), e);
			}
			if (!profile_name.equals("default")) {
				profile_name = "profile " + profile_name;
			}
			this.config_ini.add(profile_name, "region", profile.getRegion());
			this.config_ini.add(profile_name, "mfa_serial", profile.getMfaSerial());
			this.config_ini.add(profile_name, "output", profile.getOutput());
			this.config_ini.add(profile_name, "role_arn", profile.getRoleArn());
			this.config_ini.add(profile_name, "source_profile", profile.getSourceProfile());
			this.config_ini.add(profile_name, "expiration", profile.getExpiration());
			try {
				this.config_ini.store(new File(this.config_file_path));
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}

		}
	}
}

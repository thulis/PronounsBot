use std::fs;
use std::io::{self, prelude::*};
use yaml_rust::{Yaml, YamlLoader};

pub struct Config {
    pub prefix: String,
    pub token: String,
    pub pronouns: Vec<u8>,
}

impl Config {
    fn read_config_file(filename: &str) -> io::Result<Vec<Yaml>> {
        let mut file = fs::File::open(filename)?;
        let mut yaml = String::with_capacity(100);
        file.read_to_string(&mut yaml)?;

        let contents = YamlLoader::load_from_str(&yaml[..]);
        if let Err(e) = contents {
            panic!("Error parsing config file: {:?}", e);
        }

        Ok(contents.unwrap())
    }

    pub fn new(config_file: &str) -> io::Result<Self> {
        let contents = Self::read_config_file(config_file)?;
    }
}

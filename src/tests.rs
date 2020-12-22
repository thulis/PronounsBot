use yaml_rust::YamlLoader;

#[test]
fn _read_yaml() {
    let yaml_src = r#"
foo:
- list1
- list2
bar:
- 1
- 2.0
"#;
    let docs = YamlLoader::load_from_str(yaml_src).unwrap();

    assert_eq!(docs[0]["foo"][0].as_str().unwrap(), "list1");
    assert_eq!(docs[0]["bar"][1].as_f64().unwrap(), 2.0);
}

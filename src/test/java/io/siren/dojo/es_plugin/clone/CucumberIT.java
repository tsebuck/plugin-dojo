package io.siren.dojo.es_plugin.clone;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    tags = "@ok",
    features = "src/test/resources",
    plugin = {"pretty", "html:build/reports/cucumber"}
)
public class CucumberIT {
}
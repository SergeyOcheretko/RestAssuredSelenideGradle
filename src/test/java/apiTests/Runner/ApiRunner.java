package apiTests.Runner;

import apiTests.NotesApiTests.NotesApiTests;
import apiTests.UserAccountApiTests.UserAccountApiTests;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("junit-jupiter")
@SelectClasses({
        NotesApiTests.class,
        UserAccountApiTests.class
})

public class ApiRunner {}
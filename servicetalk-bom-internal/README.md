## ServiceTalk's Internal BOM

Users shouldn't directly depend upon this project, but instead use `servicetalk-bom`.

ServiceTalk has a few dependencies which are used multiple times across different repositories. This project consolidates the versions used for these common dependencies in a single location. When a version is updated, it doesn't necessarily have to result in new releases of all ServiceTalk projects. If all ServiceTalk projects were released users wouldn't know if the change actually impacts them or not. Instead when a version is updated we can selectively update ServiceTalk projects which would be impacted by the change.

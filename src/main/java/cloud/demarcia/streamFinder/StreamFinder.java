package cloud.demarcia.streamFinder;


import cloud.demarcia.model.RecordingMeta;

public interface StreamFinder {
    RecordingMeta findStream(String url);
}

package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import fr.vyfe.model.TagModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.TagRepository;
import fr.vyfe.repository.TagSetRepository;


public class PlayVideoViewModel extends VyfeViewModel {

    private MutableLiveData<Integer> videoPosition;
    private MutableLiveData<Boolean> isPlaying;
    private TagRepository tagRepository;
    private MutableLiveData<List<TagModel>> tags;
    private MutableLiveData<Integer> seekPosition;
    private MutableLiveData<Boolean> moveSeek;

    private MutableLiveData<Integer> timelinesize;
    private MutableLiveData<Integer> videoContainerHeight;
    private MutableLiveData<Integer> timelineContainerHeight;

    private MutableLiveData<Boolean> fullTimeline;
    private MutableLiveData<String> linkPlayer;
    private String androidId;


    public PlayVideoViewModel(String companyId, String userId, String sessionId, String androidId) {
        sessionRepository = new SessionRepository(companyId);
        tagSetRepository = new TagSetRepository(userId, companyId);
        tagRepository = new TagRepository(companyId, userId, sessionId);
        isPlaying = new MutableLiveData<>();
        videoPosition = new MutableLiveData<>();
        this.sessionId = sessionId;
        seekPosition = new MutableLiveData<>();
        moveSeek = new MutableLiveData<>();
        moveSeek.setValue(false);

        timelinesize = new MutableLiveData<>();
        videoContainerHeight = new MutableLiveData<>();
        timelineContainerHeight = new MutableLiveData<>();
        fullTimeline = new MutableLiveData<>();
        fullTimeline.setValue(false);
        linkPlayer = new MutableLiveData<>();
        linkPlayer.setValue(null);

        this.androidId = androidId;
    }


    public String getAndroidId(){
        return this.androidId;
    }

    public MutableLiveData<Integer> getTimelinesize() {
        return timelinesize;
    }

    public MutableLiveData<Integer> getTimelineContainerHeight() {
        return timelineContainerHeight;
    }

    public void setTimelinesize(Integer timelinesize){
        this.timelinesize.setValue(timelinesize);
    }

    public void setTimelineContainerHeight(Integer integer){
        this.timelineContainerHeight.setValue(integer);
    }

    public void setLinkPlayer(String linkPlayer){
        this.linkPlayer.setValue(linkPlayer);
    }

    public MutableLiveData<String> getLinkPlayer() {
        return linkPlayer;
    }

    public MutableLiveData<Integer> getVideoContainerHeight() {
        return videoContainerHeight;
    }

    public void setVideoContainerHeight(Integer videoContainerHeight) {
        this.videoContainerHeight.setValue(videoContainerHeight);
    }

    public void init() {
        isPlaying.setValue(false);
        this.videoPosition.setValue(0);
    }

    public LiveData<Integer> getVideoPosition() {
        return this.videoPosition;
    }

    public void setVideoPosition(Integer videoPosition) {
        this.videoPosition.setValue(videoPosition);
    }

    public MutableLiveData<Integer> getSeekPosition() {
        return seekPosition;
    }

    public void setSeekPosition(Integer seekPosition){
        this.seekPosition.setValue(seekPosition);
    }

    public LiveData<Boolean> isPlaying(){
        return isPlaying;
    }

    public MutableLiveData<Boolean> isMoveSeek() {
        return moveSeek;
    }

    public void play(){
        isPlaying.setValue(true);
    }

    public void pause(){
        isPlaying.setValue(false);
    }

    public MutableLiveData<Boolean> isFullTimeline() {
        return fullTimeline;
    }

    public void fullTimline(){fullTimeline.setValue(true);

    }
    public void smallTimeline(){
        fullTimeline.setValue(false);
    }

    public MutableLiveData<List<TagModel>> getTags() {
        if (tags == null) {
            tags = new MutableLiveData<>();
            loadTags();
        }
        return tags;
    }

    private void loadTags() {
        tagRepository.addListListener(new BaseListValueEventListener.CallbackInterface<TagModel>() {
            @Override
            public void onSuccess(List<TagModel> result) {
                tags.postValue(result);
            }

            @Override
            public void onError(Exception e) {
                tags.setValue(null);
            }
        });
    }
}

package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.view.ViewGroup;

import java.util.List;

import fr.vyfe.model.CompanyModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.UserModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.BaseSingleValueEventListener;
import fr.vyfe.repository.CompanyRepository;
import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.TagRepository;
import fr.vyfe.repository.TagSetRepository;
import fr.vyfe.repository.UserRepository;


public class PlayVideoViewModel extends VyfeViewModel {

    private MutableLiveData<Integer> videoPosition;
    private MutableLiveData<Boolean> isPlaying;
    private TagRepository tagRepository;
    private MutableLiveData<List<TagModel>> tags;
    private MutableLiveData<Integer> seekPosition;
    private MutableLiveData<Boolean> moveSeek;

    private MutableLiveData<Boolean> fullTimeline;
    private MutableLiveData<String> linkPlayer;
    private String androidId;

    private CompanyRepository companyRepository;
    private MutableLiveData<CompanyModel> company;
    private MutableLiveData<String> nameUser;
    private UserRepository userRepository;

    private MutableLiveData<Boolean> isFullScreen;
    private MutableLiveData<ViewGroup.LayoutParams> playerLayoutParams;
    private MutableLiveData<Boolean> isOpenTimelineVideo;


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

        fullTimeline = new MutableLiveData<>();
        fullTimeline.setValue(false);
        linkPlayer = new MutableLiveData<>();
        linkPlayer.setValue(null);

        this.androidId = androidId;
        companyRepository = new CompanyRepository(companyId);
        userRepository = new UserRepository(companyId);

        isFullScreen = new MutableLiveData<>();
        playerLayoutParams = new MutableLiveData<>();
        isOpenTimelineVideo = new MutableLiveData<>();
    }



    public MutableLiveData<Boolean> isOpenTimelineVideo() {
        return isOpenTimelineVideo;
    }

    public void openTimelineVideo() {
        isOpenTimelineVideo.setValue(true);
    }

    public void closeTimelineVideo() {
        isOpenTimelineVideo.setValue(false);
    }


    public MutableLiveData<ViewGroup.LayoutParams> getPlayerLayoutParamsMutableLiveData() {
        return playerLayoutParams;
    }

    public void setPlayerLayoutParamsMutableLiveData(ViewGroup.LayoutParams playerLayoutParams) {

        this.playerLayoutParams.setValue(playerLayoutParams);
    }

    public void fullMovie() {
        isFullScreen.setValue(true);
    }

    public void miniMovie() {
        isFullScreen.setValue(false);
    }

    public LiveData<Boolean> isFullScreen(){
        return isFullScreen;
    }

    public String getAndroidId(){
        return this.androidId;
    }


    public void setLinkPlayer(String linkPlayer){
        this.linkPlayer.setValue(linkPlayer);
    }

    public MutableLiveData<String> getLinkPlayer() {
        return linkPlayer;
    }


    public void init() {
        loadCompany();
        isPlaying.setValue(false);
        this.videoPosition.setValue(0);
        isFullScreen.setValue(false);
        isOpenTimelineVideo.setValue(false);
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

    public LiveData<CompanyModel> getCompany() {
        if (company == null) {
            company = new MutableLiveData<>();
            loadCompany();
        }
        return company;
    }

    public void loadCompany() {
        if (company == null) {
            company = new MutableLiveData<>();
        }
        companyRepository.addChildListener("", true, new BaseSingleValueEventListener.CallbackInterface<CompanyModel>() {
            @Override
            public void onSuccess(CompanyModel result) {
                company.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                company.setValue(null);
            }
        });
    }

}

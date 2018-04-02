package io.github.vl4fhsdatr.appflask;

import javax.inject.Singleton;

import dagger.Component;
import io.github.vl4fhsdatr.appflask.sync.AppInfoService;
import io.github.vl4fhsdatr.appflask.ui.home.browser.AppBrowserFragment;
import io.github.vl4fhsdatr.appflask.ui.home.flask.AppFlaskFragment;

@Singleton
@Component(modules={AppFlaskModule.class, PersistenceModule.class})
public interface PersistenceComponent {

    void inject(AppBrowserFragment fragment);
    void inject(AppFlaskFragment fragment);
    void inject(AppInfoService service);

}

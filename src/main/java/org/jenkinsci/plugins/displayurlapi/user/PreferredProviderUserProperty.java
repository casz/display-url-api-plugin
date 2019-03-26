package org.jenkinsci.plugins.displayurlapi.user;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jenkinsci.plugins.displayurlapi.DisplayURLProvider;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

public class PreferredProviderUserProperty extends UserProperty {

    @Extension
    public static final UserPropertyDescriptor DESCRIPTOR = new PreferredProviderUserPropertyDescriptor();

    @Nullable
    private String providerId;

    @DataBoundConstructor
    public PreferredProviderUserProperty(@Nullable String providerId) {
        this.providerId = providerId;
    }

    public ProviderOption getProvider() {
        final DisplayURLProvider provider = getConfiguredProvider();
        return provider == null ? ProviderOption.DEFAULT_OPTION : new ProviderOption(provider.getClass().getName(), provider.getDisplayName());
    }

    public DisplayURLProvider getConfiguredProvider() {
        return DisplayURLProvider.all().stream()
            .filter(input -> input.getClass().getName().equals(providerId))
            .findFirst()
            .orElse(null);
    }

    public List<ProviderOption> getAll() {
        Stream<ProviderOption> defaultOption = Stream.of(ProviderOption.DEFAULT_OPTION);
        Stream<ProviderOption> options = DisplayURLProvider.all().stream()
            .map(input -> new ProviderOption(input.getClass().getName(), input.getDisplayName()));
        Stream<ProviderOption> combined = Stream.concat(defaultOption, options);
        return ImmutableList.copyOf(combined.collect(Collectors.toList()));
    }

    public boolean isSelected(String providerId) {
        return getProvider().getId().equals(providerId);
    }

    public static class ProviderOption {

        public static final ProviderOption DEFAULT_OPTION = new ProviderOption("default", "Default");

        private final String id;
        private final String name;

        public ProviderOption(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}

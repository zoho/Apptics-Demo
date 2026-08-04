import {AppticsApiTracker} from '@zoho_apptics/apptics-react-native';
import React from 'react';

import {ActionButton} from '../components/ActionButton';
import {FeatureScaffold} from '../components/FeatureScaffold';
import {SectionCard} from '../components/SectionCard';
import {useScreenTracking} from '../core/useScreenTracking';

/**
 * Demonstrates the API call-tracking module, which measures latency, status
 * codes and failures of your network calls. Three integration strategies are
 * shown:
 *   1. Auto tracking — patches the global `fetch`, zero per-call code.
 *   2. `trackedFetch` / `createAppticsHttpClient` — explicit wrappers.
 *   3. Manual start/end — for any non-`fetch` transport.
 * Plus URL exclusion, which applies to all three.
 */
const OK_URL = 'https://jsonplaceholder.typicode.com/todos/1';
const BAD_URL = 'https://this-host-does-not-exist.invalid/x';

export function ApiTrackingScreen() {
  useScreenTracking('ApiTrackingScreen');

  return (
    <FeatureScaffold
      intro={
        'The wrapper and manual demos below make real HTTP requests — ' +
        'including a deliberately failing one — so you can see both the ' +
        'success and the error path reported.'
      }>
      <SectionCard
        title="1 · Auto tracking"
        subtitle={
          'enableAutoTracking() replaces globalThis.fetch with a tracked ' +
          'version, so every fetch in your app (and in libraries built on it) ' +
          'is measured with no extra code.'
        }>
        <ActionButton
          label="enableAutoTracking()"
          icon="autorenew"
          action={() => {
            AppticsApiTracker.enableAutoTracking();
            return 'auto tracking ON';
          }}
        />
        <ActionButton
          label="isAutoTrackingEnabled()"
          icon="help-outline"
          action={() => AppticsApiTracker.isAutoTrackingEnabled()}
        />
        <ActionButton
          label="fetch(OK_URL)  — plain global fetch"
          icon="public"
          description="Tracked only while auto tracking is on."
          action={async () => {
            const res = await fetch(OK_URL);
            const body = await res.text();
            return `HTTP ${res.status} (${body.length} bytes)`;
          }}
        />
        <ActionButton
          label="disableAutoTracking()"
          icon="sync-disabled"
          description="Restores the original fetch."
          action={() => {
            AppticsApiTracker.disableAutoTracking();
            return 'auto tracking OFF';
          }}
        />
      </SectionCard>

      <SectionCard
        title="2 · Explicit wrappers"
        subtitle={
          'trackedFetch() tracks a single call; createAppticsHttpClient() ' +
          'returns a reusable fetch-compatible client for your networking layer.'
        }>
        <ActionButton
          label="trackedFetch(OK_URL)  (success)"
          icon="cloud-done"
          action={async () => {
            const res = await AppticsApiTracker.trackedFetch(OK_URL);
            const body = await res.text();
            return `HTTP ${res.status} (${body.length} bytes)`;
          }}
        />
        <ActionButton
          label="trackedFetch(BAD_URL)  (failure / edge case)"
          icon="cloud-off"
          description="A tracked request that never resolves — the error is recorded too."
          action={async () => {
            const res = await AppticsApiTracker.trackedFetch(BAD_URL);
            return `HTTP ${res.status}`;
            // The thrown network error is logged by ActionButton's catch, and
            // the tracker records the failed call with its message.
          }}
        />
        <ActionButton
          label="createAppticsHttpClient(fetch)"
          icon="build"
          description="Build a client once, pass it into your API layer."
          action={async () => {
            const apiFetch = AppticsApiTracker.createAppticsHttpClient(fetch);
            const res = await apiFetch(OK_URL, {method: 'GET'});
            return `HTTP ${res.status} via client`;
          }}
        />
      </SectionCard>

      <SectionCard
        title="3 · Manual tracking"
        subtitle={
          'For non-fetch transports: bracket the call with startApiTracking / ' +
          'endApiTracking. startApiTracking resolves to null when the URL is ' +
          'excluded or the SDK declines — always check before ending.'
        }>
        <ActionButton
          label="startApiTracking() → endApiTracking(201)"
          icon="timeline"
          action={async () => {
            const trackId = await AppticsApiTracker.startApiTracking({
              url: 'https://api.example.com/orders',
              method: 'POST',
            });
            if (!trackId) {
              return 'not tracked (excluded or declined by the SDK)';
            }
            // ... your real request would happen here ...
            await AppticsApiTracker.endApiTracking({trackId, statusCode: 201});
            return `tracked manually (id=${trackId})`;
          }}
        />
        <ActionButton
          label="endApiTracking({ errorMessage })"
          icon="fact-check"
          description="Records a call that failed before it got a status code."
          action={async () => {
            const trackId = await AppticsApiTracker.startApiTracking({
              url: 'https://api.example.com/profile',
              method: 'GET',
            });
            if (!trackId) {
              return 'not tracked (excluded or declined by the SDK)';
            }
            await AppticsApiTracker.endApiTracking({
              trackId,
              statusCode: 500,
              errorMessage: 'Internal Server Error',
            });
            return 'failed call recorded (500)';
          }}
        />
      </SectionCard>

      <SectionCard
        title="4 · URL exclusion"
        subtitle="Any URL containing one of these substrings is skipped by all three modes.">
        <ActionButton
          label="excludedUrlPatterns.add('/healthz')"
          icon="filter-alt"
          action={() => {
            AppticsApiTracker.excludedUrlPatterns.add('/healthz');
            return `patterns: [${[
              ...AppticsApiTracker.excludedUrlPatterns,
            ].join(', ')}]`;
          }}
        />
        <ActionButton
          label="excludedUrlPatterns.add('typicode.com')"
          icon="filter-alt"
          description="Excludes the demo endpoint above — try a request afterwards."
          action={() => {
            AppticsApiTracker.excludedUrlPatterns.add('typicode.com');
            return `patterns: [${[
              ...AppticsApiTracker.excludedUrlPatterns,
            ].join(', ')}]`;
          }}
        />
        <ActionButton
          label="excludedUrlPatterns.clear()"
          icon="filter-alt-off"
          action={() => {
            AppticsApiTracker.excludedUrlPatterns.clear();
            return 'exclusions cleared';
          }}
        />
      </SectionCard>
    </FeatureScaffold>
  );
}

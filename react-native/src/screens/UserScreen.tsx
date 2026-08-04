import {
  Apptics,
  AppticsUserProperty,
} from '@zoho_apptics/apptics-react-native';
import React, {useState} from 'react';

import {ActionButton} from '../components/ActionButton';
import {FeatureScaffold} from '../components/FeatureScaffold';
import {LabeledInput} from '../components/LabeledInput';
import {SectionCard} from '../components/SectionCard';
import {useScreenTracking} from '../core/useScreenTracking';

/**
 * Demonstrates user identity and user-property APIs: `setUser`,
 * `setUserWithOrgId` and `isUserLoggedIn`.
 *
 * A user property object mixes Apptics' predefined keys (`first_name`,
 * `plan_type`, …) with any custom keys you want; values may be strings,
 * numbers or booleans.
 */
export function UserScreen() {
  useScreenTracking('UserScreen');

  const [userId, setUserId] = useState('user@example.com');
  const [orgId, setOrgId] = useState('acme-corp');

  const buildProperties = (): AppticsUserProperty => ({
    // Predefined properties recognised by Apptics.
    first_name: 'Ada',
    last_name: 'Lovelace',
    email_address: userId,
    company_name: 'Analytical Engines',
    plan_type: 'enterprise',
    country: 'UK',
    // Custom properties — up to 30 per project.
    referral: 'newsletter',
    seats: 25,
    beta_optin: true,
  });

  return (
    <FeatureScaffold
      intro={
        'Identify the signed-in user so analytics, crashes and feedback are ' +
        'attributed correctly. The user id is PII: it is only associated with ' +
        'stats when the tracking state allows PII (see the Privacy screen).'
      }>
      <SectionCard title="Identity inputs">
        <LabeledInput
          label="User ID"
          value={userId}
          onChangeText={setUserId}
          helperText="Try a real value, then try clearing it."
        />
        <LabeledInput label="Org ID" value={orgId} onChangeText={setOrgId} />
      </SectionCard>

      <SectionCard title="Identify">
        <ActionButton
          label="setUser(userId)"
          icon="person-add"
          description="Ties the user id to events, screens and crashes."
          action={() => {
            Apptics.setUser(userId);
            return 'user set';
          }}
        />
        <ActionButton
          label="setUser(userId, properties)"
          icon="badge"
          description="Sets the user along with profile properties."
          action={() => {
            Apptics.setUser(userId, buildProperties());
            return 'user + properties set';
          }}
        />
        <ActionButton
          label="setUserWithOrgId(userId, orgId, properties)"
          icon="corporate-fare"
          description="Adds an organization / tenant id to the association."
          action={() => {
            Apptics.setUserWithOrgId(userId, orgId, buildProperties());
            return 'user + org + properties set';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Query"
        subtitle="This returns a value — watch it in the console.">
        <ActionButton
          label="isUserLoggedIn()"
          icon="how-to-reg"
          description="Resolves true once a user id has been set."
          action={() => Apptics.isUserLoggedIn()}
        />
      </SectionCard>

      <SectionCard
        title="Limits"
        subtitle={
          'Up to 30 unique custom properties per project · keys ≤ 50 chars · ' +
          'values ≤ 250 chars. Values must be string, number or boolean.'
        }
      />
    </FeatureScaffold>
  );
}

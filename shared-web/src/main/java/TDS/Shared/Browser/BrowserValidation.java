/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Browser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import AIR.Common.Web.BrowserOS;

/**
 * @author mpatel
 *
 */
public class BrowserValidation
{
  private static final String WILDCARD = "*";
  private final List<BrowserRule> _rules = new ArrayList<BrowserRule>(); 
  
  public List<BrowserRule> GetRules()
  {
      return _rules;
  }

  public void AddRule(BrowserRule browserRule)
  {
      _rules.add(browserRule);
  }

  /// <summary>
  /// Check if the browser user agent string matches this rule.
  /// </summary>
  private boolean MatchRule(BrowserInfo browserInfo, BrowserRule browserRule)
  {
      // check if OS name matches ('*' will be Unknown, which means skip this check)
      if (browserRule.getOsName () != BrowserOS.Unknown &&
          browserInfo.getOSName ()  != browserRule.getOsName () ) return false;

      // check if OS meets minimum version (0 means skip this check)
      if (browserRule.getOsMinVersion () > 0 &&
          browserInfo.getOSVersion () < browserRule.getOsMinVersion ()) return false;

      // check if OS meets maximum version (0 means skip this check)
      if (browserRule.getOsMaxVersion () > 0 &&
          browserInfo.getOSVersion () > browserRule.getOsMaxVersion ()) return false;

      // check if the hardware architecture matches ('*' means skip this check)
      if (!browserRule.getArchitecture ().equals(WILDCARD) &&
          !browserInfo.getArchitecture ().equals (browserRule.getArchitecture ())) return false;

      // check if the browser name matches ('*' means skip this check)
      if (!browserRule.getName ().equals(WILDCARD) &&
          !browserInfo.getName ().equals (browserRule.getName ())) return false;

      // check if browser meets minimum version (0 means skip this check)
      if (browserRule.getMinVersion ()  > 0 &&
          browserInfo.getVersion () < browserRule.getMinVersion ()) return false;

      // check if browser meets maximum version (0 means skip this check)
      if (browserRule.getMaxVersion () > 0 &&
          browserInfo.getVersion () > browserRule.getMaxVersion ()) return false;

      // everything matched
      return true;
  }

  /// <summary>
  /// Find all the matching browser rules for this user agent.
  /// </summary>
  public List<BrowserRule> FindRules(final BrowserInfo browserInfo)
  {
//      return _rules.Where(r => MatchRule(browserInfo, r)).OrderByDescending(r => r.Priority);
      CollectionUtils.filter (_rules, new Predicate()
      {
        @Override
        public boolean evaluate (Object object) {
          // TODO Auto-generated method stub
          return MatchRule (browserInfo, (BrowserRule)object);
        }
      });
      if(_rules!=null && !_rules.isEmpty ()) {
        java.util.Collections.sort (_rules, new Comparator<BrowserRule>()
        {
          @Override
          public int compare (BrowserRule o1, BrowserRule o2) {
            return Integer.compare (o1.getPriority (), o2.getPriority ());
          }
          
        });
        return _rules;
      } else {
        return null;
      }
  }

  /// <summary>
  /// Find the highest priority browser rule for this user agent.
  /// </summary>
  /// <returns>Either the rule or NULL if none found.</returns>
  public BrowserRule FindRule(BrowserInfo browserInfo)
  {
      List<BrowserRule>  browserRulesList = FindRules(browserInfo);
      if(browserRulesList!=null && !browserRulesList.isEmpty ()) {
        return browserRulesList.get (0);
      }
      else { 
        return null;
      }
  }

  /// <summary>
  /// Check the action for this user agent string.
  /// </summary>
  public BrowserAction Check(BrowserInfo browserInfo)
  {
      BrowserRule browserRule = FindRule(browserInfo);
      return (browserRule != null) ? browserRule.getAction () : BrowserAction.Deny;
  }
}

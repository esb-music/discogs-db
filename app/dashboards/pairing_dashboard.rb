# frozen_string_literal: true
require "administrate/custom_dashboard"

class PairingDashboard < Administrate::CustomDashboard
  resource "Pairings"
end

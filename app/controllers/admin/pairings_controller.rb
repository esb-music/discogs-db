# frozen_string_literal: true

module Admin
  class PairingsController < Admin::ApplicationController
    def create
      render :index
    end

    def index
      musician1 = params['musician1']
      musician2 = params['musician2']
      if musician1 and musician2
        @musician1 = Musician.find(musician1).name
        @musician2 = Musician.find(musician2).name
        @tracks = Track.get_pairings(musician1, musician2)
      render :pairings
      end
    end
  end
end
